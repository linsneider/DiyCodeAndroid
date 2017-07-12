package com.sneider.diycode.mvp.model.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Section implements Serializable {

    private int id;
    private String name;
    private List<Node> nodes = new ArrayList();
    private boolean selected;

    public Section(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Section) {
            Section section = (Section) obj;
            return section.getId() == this.getId();
        } else {
            return false;
        }
    }
}
