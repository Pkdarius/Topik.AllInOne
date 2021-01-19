package com.haitd.topik.sub;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.haitd.topik.entity.Account;
import com.haitd.topik.entity.ConsoleColors;
import com.haitd.topik.handler.ReadDataSaved;
import com.haitd.topik.handler.SaveAccountToFile;
import com.haitd.topik.serializable.Accounts;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class CreateInstances {
    private final int numberInstancesPerRequest = 3;
    private final int filterLevel;
    private final AmazonEC2 amazonEC2Client;
    private int numberOfRequest;
    private final List<String> instanceIds = new ArrayList<>();

    public CreateInstances(int filterLevel) {
        this.filterLevel = filterLevel;
        this.amazonEC2Client = AmazonEC2ClientBuilder.standard()
                .withRegion(Regions.AP_NORTHEAST_2)
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("AKIAUXMHOPNY3SQ25LF5", "G5nj1A5ZpOg9Bx1nMtj9sevrMGpOzAhG+qdmWEnq ")))
                .build();
    }

    public void start() throws Exception {
        Accounts savedAccounts = (Accounts) ReadDataSaved.read("./resources/serialization/accounts-" + filterLevel + ".ser");
        if (savedAccounts == null) {
            System.out.println("No data");
            return;
        }
        List<Account> accounts = savedAccounts.getAccount();

        if (accounts.stream().noneMatch(account -> account.getInstanceId() == null)) {
            System.out.println("No data or no instance to start");
            return;
        }

        int numberAccounts = accounts.size();
        numberOfRequest = numberAccounts % numberInstancesPerRequest == 0 ? numberAccounts / numberInstancesPerRequest : numberAccounts / numberInstancesPerRequest + 1;

        for (int i = 0; i < numberOfRequest; i++) {
            RunInstancesRequest runInstancesRequest = new RunInstancesRequest()
                    .withImageId("ami-02d3950d64c45857a")
                    .withInstanceType(InstanceType.T2Micro)
                    .withMinCount(numberInstancesPerRequest)
                    .withMaxCount(numberInstancesPerRequest)
                    .withKeyName("topik-proxy")
                    .withSecurityGroups("JavaSecurityGroup_OpenHttp");
            RunInstancesResult result1 = amazonEC2Client.runInstances(runInstancesRequest);
            List<Instance> instances = result1.getReservation().getInstances();
            instanceIds.addAll(instances.stream().map(Instance::getInstanceId).collect(Collectors.toList()));
            Thread.sleep(1000);
        }

        for (int i = 0; i < numberOfRequest; i++) {
            List<String> requestIds = instanceIds.subList(i * numberInstancesPerRequest, (i + 1) * numberInstancesPerRequest);
            StartInstancesRequest startInstancesRequest = new StartInstancesRequest()
                    .withInstanceIds(requestIds);

            StartInstancesResult startInstancesResult = amazonEC2Client.startInstances(startInstancesRequest);
            List<InstanceStateChange> instances = startInstancesResult.getStartingInstances();
            System.out.println("Start " + instances.size() + " instances");
            Thread.sleep(1000);
        }

        while (true) {
            int count = 0;
            for (int i = 0; i < numberOfRequest; i++) {
                List<String> requestIds = instanceIds.subList(i * numberInstancesPerRequest, (i + 1) * numberInstancesPerRequest);
                DescribeInstanceStatusRequest describeInstanceStatusRequest = new DescribeInstanceStatusRequest()
                        .withInstanceIds(requestIds);
                DescribeInstanceStatusResult describeInstancesResult = amazonEC2Client.describeInstanceStatus(describeInstanceStatusRequest);
                List<InstanceStatus> instanceStatuses = describeInstancesResult.getInstanceStatuses();
                for (InstanceStatus instanceStatus : instanceStatuses) {
                    if (instanceStatus.getInstanceState().getName().equals("running")) {
                        count++;
                    }
                }
            }
            if (count == numberOfRequest * numberInstancesPerRequest) {
                break;
            }
        }
        System.out.println(ConsoleColors.ANSI_GREEN_BACKGROUND + "All instances are running" + ConsoleColors.ANSI_RESET);

        List<Instance> activatedInstances = new ArrayList<>();
        for (int i = 0; i < numberOfRequest; i++) {
            List<String> requestIds = instanceIds.subList(i * numberInstancesPerRequest, (i + 1) * numberInstancesPerRequest);
            DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest()
                    .withInstanceIds(requestIds);

            DescribeInstancesResult describeInstancesResult = amazonEC2Client.describeInstances(describeInstancesRequest);
            List<Reservation> reservations = describeInstancesResult.getReservations();
            for (Reservation reservation : reservations) {
                activatedInstances.addAll(reservation.getInstances());
            }
        }
        System.out.println(ConsoleColors.ANSI_GREEN_BACKGROUND + "Activated instances: " + activatedInstances.size() + ConsoleColors.ANSI_RESET);

        for (int i = 0; i < accounts.size(); i++) {
            accounts.get(i).setIpAddress(activatedInstances.get(i).getPublicIpAddress());
            accounts.get(i).setInstanceId(activatedInstances.get(i).getInstanceId());
        }

        for (Account account : accounts) {
            System.out.println("IP: " + account.getIpAddress());
        }
        SaveAccountToFile.saveAccount(accounts, null, filterLevel);
    }

    public void stop() {
        Accounts savedAccounts = (Accounts) ReadDataSaved.read("./resources/serialization/accounts-" + filterLevel + ".ser");

        if (savedAccounts == null) {
            System.out.println("No data");
            return;
        }
        List<Account> accounts = savedAccounts.getAccount();

        if (accounts.stream().noneMatch(account -> account.getInstanceId() != null)) {
            System.out.println("No data or no instance to start");
            return;
        }

        int numberAccounts = accounts.size();
        numberOfRequest = numberAccounts % numberInstancesPerRequest == 0 ? numberAccounts / numberInstancesPerRequest : numberAccounts / numberInstancesPerRequest + 1;

        for (int i = 0; i < numberOfRequest; i++) {
            List<Account> subList = accounts.subList(i * numberInstancesPerRequest, Math.min((i + 1) * numberInstancesPerRequest, numberAccounts - 1));
            TerminateInstancesRequest terminateInstancesRequest = new TerminateInstancesRequest().withInstanceIds(subList.stream().map(Account::getInstanceId).collect(Collectors.toList()));
            TerminateInstancesResult terminateInstancesResult = amazonEC2Client.terminateInstances(terminateInstancesRequest);
            List<InstanceStateChange> terminatingInstances = terminateInstancesResult.getTerminatingInstances();
            System.out.println(ConsoleColors.ANSI_GREEN_BACKGROUND + "Stopped " + terminatingInstances.size() + ConsoleColors.ANSI_RESET);
            subList.forEach(account -> account.setInstanceId(null));
        }
        SaveAccountToFile.saveAccount(accounts, null, filterLevel);
    }
}