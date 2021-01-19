package com.haitd.topik.serializable;

import java.io.Serializable;
import java.util.List;

public class InstanceIds implements Serializable {
    List<String> instancesIds;

    public InstanceIds(List<String> instancesIds) {
        this.instancesIds = instancesIds;
    }

    public List<String> getInstancesIds() {
        return instancesIds;
    }

    public void setInstancesIds(List<String> instancesIds) {
        this.instancesIds = instancesIds;
    }
}
