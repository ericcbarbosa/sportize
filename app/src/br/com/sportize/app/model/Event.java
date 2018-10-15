package br.com.sportize.app.model;

public class Event {
    private String id;
    private String groupId;
    private String name;
    private String description;
    private String occurrenceDate;
    private String occurrenceTime;
    private String address;
    private String neighborhood;
    private String city;
    private String state;

    public Event(String id, String groupId, String name, String description, String occurrenceDate, String occurrenceTime, String address, String neighborhood, String city, String state) {
        this.id = id;
        this.groupId = groupId;
        this.name = name;
        this.description = description;
        this.occurrenceDate = occurrenceDate;
        this.occurrenceTime = occurrenceTime;
        this.address = address;
        this.neighborhood = neighborhood;
        this.city = city;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOccurrenceDate() {
        return occurrenceDate;
    }

    public void setOccurrenceDate(String occurrenceDate) {
        this.occurrenceDate = occurrenceDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getOccurrenceTime() {
        return occurrenceTime;
    }

    public void setOccurrenceTime(String occurrenceTime) {
        this.occurrenceTime = occurrenceTime;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", groupId=" + groupId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", occurrenceDate=" + occurrenceDate +
                ", address='" + address + '\'' +
                ", neighborhood='" + neighborhood + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
