package com.task.dto.request;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "command")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlCommand {

    @XmlAttribute(name = "id")
    private String id;

    @XmlElement(name = "get")
    private GetCommand get;

    @XmlElement(name = "history")
    private HistoryCommand history;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public GetCommand getGet() {
        return get;
    }

    public void setGet(GetCommand get) {
        this.get = get;
    }

    public HistoryCommand getHistory() {
        return history;
    }

    public void setHistory(HistoryCommand history) {
        this.history = history;
    }

}