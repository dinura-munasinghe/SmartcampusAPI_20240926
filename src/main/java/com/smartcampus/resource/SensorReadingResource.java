package com.smartcampus.resource;

import com.smartcampus.model.ErrorResponse;
import com.smartcampus.model.SensorReading;
import com.smartcampus.service.SensorReadingService;
import com.smartcampus.service.SensorService;
import java.net.URI;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;
    private final SensorService sensorService = new SensorService();
    private final SensorReadingService sensorReadingService = new SensorReadingService();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public Response getReadings() {
        if (sensorService.getSensor(sensorId) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(404, "Not Found", "Sensor " + sensorId + " was not found."))
                    .build();
        }
        return Response.ok(sensorReadingService.getReadings(sensorId)).build();
    }

    @POST
    public Response addReading(SensorReading reading, @Context UriInfo uriInfo) {
        if (reading == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(400, "Bad Request", "Reading JSON body is required."))
                    .build();
        }

        if (sensorService.getSensor(sensorId) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(404, "Not Found", "Sensor " + sensorId + " was not found."))
                    .build();
        }

        SensorReading createdReading = sensorReadingService.addReading(sensorId, reading);
        URI uri = uriInfo.getAbsolutePathBuilder().path(createdReading.getId()).build();
        return Response.created(uri).entity(createdReading).build();
    }
}
