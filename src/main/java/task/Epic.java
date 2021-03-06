package task;

import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<Integer> subTaskIdList;

    public Epic(int id, String name, String description, List<Integer> subTaskIdList) {
        super(id, name, description);
        this.subTaskIdList = subTaskIdList;
        this.typeTask = TypeTask.EPIC;
    }

    public List<Integer> getSubTaskIdList() {
        return subTaskIdList;
    }

    public void setSubTaskIdList(List<Integer> subTaskIdList) {
        this.subTaskIdList = subTaskIdList;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", type=" + typeTask +
                ", name=" + name +
                ", description=" + description +
                ", status=" + status +
                ", subTaskIdList=" + subTaskIdList +
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return subTaskIdList.equals(epic.subTaskIdList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTaskIdList);
    }
}
