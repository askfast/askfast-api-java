package com.askfast.util;

import java.util.List;
import java.util.Set;
import com.askfast.model.Adapter;
import com.askfast.model.DDRRecord;
import com.askfast.model.Dialog;
import com.askfast.model.DialogRequest;
import com.askfast.model.Recording;
import com.askfast.model.RestResponse;
import com.askfast.model.Result;
import com.askfast.model.TTSUser;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

public interface AskFastRestService {

    // Starting a dialog
    @POST("/startDialog")
    public Result startDialog(@Body DialogRequest req);

    // Adapter calls	
    @GET("/adapter")
    public Set<Adapter> getAdapters(@Query("type") String type, @Query("owned") boolean owned);
 
    // Adapter calls        
    @GET("/adapter/{adapterId}")
    public Adapter getAdapter(@Path("adapterId") String adapterId);

    @POST("/adapter/{adapterId}")
    public Response buyAdapter(@Path("adapterId") String adapterId);

    @PUT("/adapter/{adapterId}")
    Adapter updateAdapter(@Path("adapterId") String adapterId, @Body Adapter adapter);

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

    // TTS calls
    @GET("/tts")
    public List<TTSUser> getTTSAccounts();

    @GET("/tts/{ttsAccountId}")
    public TTSUser getTTSAccount(@Path("ttsAccountId") String ttsAccountId);

    @POST("/tts")
    public TTSUser createTTSAccount(@Body TTSUser ttsUser);

    @PUT("/tts/{ttsAccountId}")
    public TTSUser updateTTSAccount(@Path("ttsAccountId") String ttsAccountId, @Body TTSUser ttsUser);

    @DELETE("/tts/{ttsAccountId}")
    public void deleteTTSAccount(@Path("ttsAccountId") String ttsAccountId);

    // Recording calls
    @GET("/account/{accountId}/recording")
    public List<Recording> getRecordings(@Path("accountId") String accountId);

    // ----------------------------------------------DDR calls ----------------------------------------------
    /**
     * Returns a list of {@link DDRRecord DDRRecords} based on the give
     * parameters.
     * 
     * @param delimitedAdapterIds
     *            The list of adapterIds for which ddrRecords must be fetched
     * @param delimitedAdapterTypes
     *            Fetch ddrRecords based on the channel used
     * @param fromAddress
     *            Fetch ddrRecords based on the fromAddress
     * @param typeId
     *            Fetch ddrRecords based on the type of cost attached. E.g.
     *            incoming, outgoing, tts processing etc
     * @param status
     *            Fetch ddrRecords based on the status of the communication
     * @param startTime
     *            Fetch ddrRecords based on a date range
     * @param endTime
     *            Fetch ddrRecords based on a date range
     * @param delimitedSessionKeys
     *            Fetch ddrRecords based on the sessionKeys generated per
     *            communication address.
     * @param offset
     *            Skip the given number of ddrRecords
     * @param limit
     *            Limit the fetch
     * @param shouldGenerateCosts
     *            Force recalculating the costs for the communication or event
     * @param shouldIncludeServiceCosts
     *            Force recalculating the service costs for the communication or
     *            event
     * @return A collection of ddrRecords
     */
    @GET("/ddr")
    public List<DDRRecord> getDDRRecords(@Query("adapterIds") String delimitedAdapterIds,
        @Query("adapterTypes") String delimitedAdapterTypes, @Query("fromAddress") String fromAddress,
        @Query("typeId") String typeId, @Query("status") String status, @Query("startTime") Long startTime,
        @Query("endTime") Long endTime, @Query("sessionKeys") String delimitedSessionKeys,
        @Query("offset") Integer offset, @Query("limit") Integer limit,
        @Query("shouldGenerateCosts") Boolean shouldGenerateCosts,
        @Query("shouldIncludeServiceCosts") Boolean shouldIncludeServiceCosts);

    /**
     * A faster fetch of the aggregation of all quantities in the
     * {@link DDRRecord#getQuantity()} based on the filtering criteria given
     * 
     * @param delimitedAdapterIds
     *            The list of adapterIds for which ddrRecords must be fetched
     * @param delimitedAdapterTypes
     *            Fetch ddrRecords based on the channel used
     * @param fromAddress
     *            Fetch ddrRecords based on the fromAddress
     * @param typeId
     *            Fetch ddrRecords based on the type of cost attached. E.g.
     *            incoming, outgoing, tts processing etc
     * @param status
     *            Fetch ddrRecords based on the status of the communication
     * @param startTime
     *            Fetch ddrRecords based on a date range
     * @param endTime
     *            Fetch ddrRecords based on a date range
     * @param delimitedSessionKeys
     *            Fetch ddrRecords based on the sessionKeys generated per
     *            communication address.
     * @param offset
     *            Skip the given number of ddrRecords
     * @return Total number of ddrs fetched for the filter passed
     */
    @GET("/ddr/count")
    public RestResponse getDDRRecordsCount(@Query("adapterIds") String delimitedAdapterIds,
        @Query("adapterTypes") String delimitedAdapterTypes, @Query("fromAddress") String fromAddress,
        @Query("typeId") String typeId, @Query("status") String status, @Query("startTime") Long startTime,
        @Query("endTime") Long endTime, @Query("sessionKeys") String delimitedSessionKeys,
        @Query("offset") Integer offset);
}
