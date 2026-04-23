package com.smartcampus.resource;

import com.smartcampus.model.ErrorResponse;
import com.smartcampus.model.Room;
import com.smartcampus.service.RoomService;
import java.net.URI;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Context;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    private final RoomService roomService = new RoomService();

    @GET
    public Response getRooms() {
        return Response.ok(roomService.getAllRooms()).build();
    }

    @POST
    public Response addRoom(Room room, @Context UriInfo uriInfo) {
        if (room == null || room.getId() == null || room.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(400, "Bad Request", "Room id is required. Send a valid JSON room body."))
                    .build();
        }

        Room createdRoom = roomService.addRoom(room);
        URI uri = uriInfo.getAbsolutePathBuilder().path(createdRoom.getId()).build();
        return Response.created(uri).entity(createdRoom).build();
    }

    @GET
    @Path("/{roomId}")
    public Response getRoom(@PathParam("roomId") String roomId) {
        Room room = roomService.getRoom(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(404, "Not Found", "Room " + roomId + " was not found."))
                    .build();
        }
        return Response.ok(room).build();
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        boolean deleted = roomService.deleteRoom(roomId);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(404, "Not Found", "Room " + roomId + " was not found."))
                    .build();
        }
        return Response.noContent().build();
    }
}
