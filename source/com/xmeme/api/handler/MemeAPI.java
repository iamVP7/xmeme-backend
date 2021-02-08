package com.xmeme.api.handler;


import com.xmeme.api.processor.MemeAPIHandler;
import com.xmeme.clientobjects.ClientMeme;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("memes") // NO I18N
public class MemeAPI {
    private static final String URL_FETCH_SPECIFIC_MEME = "/{meme_id}"; // NO I18N
    private static final String URL_MORE_MEMES = "/pageorder/{page_order_id}"; // NO I18N

    private static final String PP_MEME_ID = "meme_id"; // NO I18N
    private static final String PP_PAGE_ORDER_ID = "page_order_id"; // NO I18N

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMemes() {
        return new MemeAPIHandler().getAllMemes(null);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(URL_MORE_MEMES)
    public Response getMoreMemes(@PathParam(PP_PAGE_ORDER_ID) String pageOrderID) {
        return new MemeAPIHandler().getAllMemes(pageOrderID);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUserProfile(ClientMeme clientClientMeme) {
        return new MemeAPIHandler().createMeme(clientClientMeme);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(URL_FETCH_SPECIFIC_MEME)
    public Response getSpecificMeme(@PathParam(PP_MEME_ID) String memeID) {
        return new MemeAPIHandler().getSpecificMeme(memeID);
    }
}
