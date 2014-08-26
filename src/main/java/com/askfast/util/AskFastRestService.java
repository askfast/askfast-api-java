package com.askfast.util;

import com.askfast.model.DialogRequest;

import retrofit.http.Body;
import retrofit.http.POST;

public interface AskFastRestService {

	@POST("/startDialog")
	public String startDialog(@Body DialogRequest req);
}
