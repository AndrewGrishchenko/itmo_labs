package lab6_server.models;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class TicketMixin {
    @JsonIgnore private int id;
    @JsonIgnore private LocalDateTime creationDate;
}
