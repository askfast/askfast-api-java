package com.askfast.util;

import java.util.List;
import java.util.Set;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import com.askfast.model.Adapter;
import com.askfast.model.DDRRecord;
import com.askfast.model.Dialog;
import com.askfast.model.DialogRequest;

public interface AskFastRestService {

        // Starting a dialog
    
	@POST("/startDialog")
	public String startDialog(@Body DialogRequest req);
	
	// Adapter calls	
	@GET("/adapter")
	public Set<Adapter> getAdapters(@Query("type") String type);

	@POST("/adapter/{adapterId}")
        public Response buyAdapter(@Path("adapterId") String adapterId);
	
	@GET("/free_adapters")
	public Set<Adapter> getFreeAdapters(@Query("adapterType") String adapterType, @Query("address") String address);
	
	@DELETE("/adapter/{adapterId}")
	public Response removeAdapter(@Path("adapterId") String adapterId);
	
	// Dialog calls
	@GET("/dialog")
        public Set<Dialog> getDialogs();
	
	@GET("/dialog/{dialogId}")
        public Dialog getDialog(@Path("dialogId") String dialogId);
        
	@POST("/dialog")
        public Dialog createDialog(@Body Dialog dialog);
	
	@PUT("/dialog/{dialogId}")
        public Dialog updateDialog(@Path("dialogId") String dialogId, @Body Dialog dialog);
	
	@DELETE("/dialog/{dialogId}")
        public Response removeDialog(@Path("dialogId") String dialogId);
	
        // DDR calls
        @GET("/ddr")
        public List<DDRRecord> getDDRRecords(@Query("adapterIds") String delimitedAdapterIds,
            @Query("adapterTypes") String delimitedAdapterTypes, @Query("fromAddress") String fromAddress,
            @Query("typeId") String typeId, @Query("status") String status, @Query("startTime") Long startTime,
            @Query("endTime") Long endTime, @Query("sessionKeys") String delimitedSessionKeys,
            @Query("offset") Integer offset, @Query("limit") Integer limit,
            @Query("shouldGenerateCosts") Boolean shouldGenerateCosts,
            @Query("shouldIncludeServiceCosts") Boolean shouldIncludeServiceCosts);

	@PUT("/adapter/{adapterId}")
	Adapter updateAdapter(@Path("adapterId") String adapterId, @Body Adapter adapter);
}
