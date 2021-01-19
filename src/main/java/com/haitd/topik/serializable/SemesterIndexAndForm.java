package com.haitd.topik.serializable;

import java.io.Serializable;
import java.util.Map;

public class SemesterIndexAndForm implements Serializable {
    private String semesterIndex;
    private Map<String, String> inputNameAndValuePair;

    public SemesterIndexAndForm(String semesterIndex, Map<String, String> inputNameAndValuePair) {
        this.semesterIndex = semesterIndex;
        this.inputNameAndValuePair = inputNameAndValuePair;
    }

    public String getSemesterIndex() {
        return semesterIndex;
    }

    public void setSemesterIndex(String semesterIndex) {
        this.semesterIndex = semesterIndex;
    }

    public Map<String, String> getInputNameAndValuePair() {
        return inputNameAndValuePair;
    }

    public void setInputNameAndValuePair(Map<String, String> inputNameAndValuePair) {
        this.inputNameAndValuePair = inputNameAndValuePair;
    }
}
