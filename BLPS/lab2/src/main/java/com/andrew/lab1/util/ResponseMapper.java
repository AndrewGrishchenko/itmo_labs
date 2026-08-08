package com.andrew.lab1.util;

import com.andrew.lab1.dto.complaint.ComplaintResponse;
import com.andrew.lab1.dto.music.MusicResponse;
import com.andrew.lab1.dto.user.UserResponse;
import com.andrew.lab1.dto.video.VideoResponse;
import com.andrew.lab1.entity.Complaint;
import com.andrew.lab1.entity.Music;
import com.andrew.lab1.entity.Video;
import com.andrew.lab1.entity.XmlUser;

public class ResponseMapper {
    public static UserResponse toResponse(XmlUser user) {
        return new UserResponse(
            user.getId(),
            user.getUsername()
        );
    }

    public static VideoResponse toResponse(Video video) {
        return new VideoResponse(
            video.getId(),
            video.getTitle(),
            video.getAuthorId(),
            video.getStatus(),
            video.getMusic()
        );
    }

    public static MusicResponse toResponse(Music music) {
        return new MusicResponse(
            music.getId(),
            music.getName(),
            music.getRightsholderId()
        );
    }

    public static ComplaintResponse toResponse(Complaint complaint) {
        return new ComplaintResponse(
            complaint.getId(),
            toResponse(complaint.getVideo()),
            complaint.getRightsholderId(),
            complaint.getClaimDetails(),
            complaint.getStatus(),
            complaint.getModeratorComment()
        );
    }
}
