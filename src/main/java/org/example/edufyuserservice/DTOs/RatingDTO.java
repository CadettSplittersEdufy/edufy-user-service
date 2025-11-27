package org.example.edufyuserservice.DTOs;

public class RatingDTO {
    private Long id;

    private Long userId;

    private String ratingType;

    private String mediaType;

    private Long mediaId;


    public RatingDTO() {}

    public RatingDTO(Long userId, String ratingType, String mediaType, Long mediaId) {
        this.userId = userId;
        this.ratingType = ratingType;
        this.mediaType = mediaType;
        this.mediaId = mediaId;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getRatingType() {
        return ratingType;
    }

    public void setRatingType(String ratingType) {
        this.ratingType = ratingType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMediaId() {
        return mediaId;
    }

    public void setMediaId(Long mediaId) {
        this.mediaId = mediaId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "RatingDTO [userId=" + userId + ", ratingType=" + ratingType + ", mediaType=" + mediaType + ", mediaId=" + mediaId + "]";
    }
}
