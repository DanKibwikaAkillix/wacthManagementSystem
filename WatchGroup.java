public class WatchGroup {
    private int watchGroupId;
    private int eventId;
    private int organizerId;
    private String title;
    private String description;
    private java.sql.Timestamp eventDate;
    private String location;
    private java.sql.Timestamp createdAt;

    // Default constructor
    public WatchGroup() {}

    // Parameterized constructor
    public WatchGroup(int watchGroupId, int eventId, int organizerId, String title, String description,
                      java.sql.Timestamp eventDate, String location, java.sql.Timestamp createdAt) {
        this.watchGroupId = watchGroupId;
        this.eventId = eventId;
        this.organizerId = organizerId;
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.location = location;
        this.createdAt = createdAt;
    }

    public int getWatchGroupId() {
        return watchGroupId;
    }

    public void setWatchGroupId(int watchGroupId) {
        this.watchGroupId = watchGroupId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(int organizerId) {
        this.organizerId = organizerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public java.sql.Timestamp getEventDate() {
        return eventDate;
    }

    public void setEventDate(java.sql.Timestamp eventDate) {
        this.eventDate = eventDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public java.sql.Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.sql.Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
