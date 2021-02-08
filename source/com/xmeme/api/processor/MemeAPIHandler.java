package com.xmeme.api.processor;

import com.xmeme.clientobjects.ClientMeme;
import com.xmeme.handler.MemeCreatorManager;
import com.xmeme.handler.MemeManager;
import com.xmeme.pojo.MemeCreator;
import com.xmeme.pojo.Memes;
import com.xmeme.utils.APIErrorMessage;
import com.xmeme.utils.Constants;
import com.xmeme.utils.IOCommonUtil;
import com.xmeme.utils.ResponseConstructors;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.core.Response;
import java.util.List;

public class MemeAPIHandler {

    /**
     * @param clientClientMeme is the client JSON converted object
     * @return 409 in case same user enters same URL & Captin
     */
    public Response createMeme(ClientMeme clientClientMeme) {

        if (IOCommonUtil.isValidObject(clientClientMeme)) {
            try {
                clientClientMeme.validate();
                MemeCreator memeCreator = getMemerCreator(clientClientMeme);
                Memes createdMeme = checkAndCreateMeme(clientClientMeme, memeCreator);
                if (IOCommonUtil.isValidObject(createdMeme)) {
                    return getMemeCreatedResponse(createdMeme);
                }
            } catch (XMemeException exception) {
                return ResponseConstructors.getExceptionBaseResponse(exception);
            }
        }
        return ResponseConstructors.getExceptionBaseResponse(APIErrorMessage.ERRORCODE.INTERNAL_ERROR);
    }

    private Response getMemeCreatedResponse(Memes createdMeme) {
        JSONObject responseObject = IOCommonUtil.addJSONKeyValue(null, Constants.CLIENT_MEME_ID, createdMeme.getMemeId());
        ResponseConstructors responseConstructor = new ResponseConstructors.ResponseBuilder()
                .setResponseCode(Constants.HTTP_CREATE_SUCCESS)
                .setClientJSON(responseObject)
                .build();
        return responseConstructor.getResponse();
    }

    private Memes checkAndCreateMeme(final ClientMeme clientClientMeme, final MemeCreator memeCreator) throws XMemeException {
        Memes existingMeme = getExistingMeme(clientClientMeme, memeCreator);
        if (IOCommonUtil.isValidObject(existingMeme) && IOCommonUtil.isValidLong(existingMeme.getMemeId())) {
            throw new XMemeException.MemeAlreadyExist();
        }
        return createMeme(clientClientMeme, memeCreator);
    }

    private MemeCreator getMemerCreator(ClientMeme clientClientMeme) {
        MemeCreator creator = null;
        try {
            creator = new MemeCreatorManager().getCreator(clientClientMeme.getName());
            System.out.println("the creator id is:::" + creator.getOwnerID()
            );
        } catch (Exception exception) {
            System.out.println("No Creator founds");
        }
        if (!IOCommonUtil.isValidObject(creator)) {
            creator = new MemeCreatorManager().addMemeCreator(clientClientMeme.getName());
        }
        return creator;
    }

    private Memes getExistingMeme(final ClientMeme clientClientMeme, final MemeCreator memeCreator) {
        try {
            return new MemeManager().getExistingMeme(clientClientMeme, memeCreator, Constants.DEFAULT_LONG);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
        return null;
    }

    private Memes createMeme(final ClientMeme clientClientMeme, final MemeCreator memeCreator) {
        try {
            return new MemeManager().addMeme(clientClientMeme, memeCreator);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
        return null;
    }

    public Response getAllMemes(final String pageOrder) {
        List<Memes> allExisitingMemes = new MemeManager().getAllMemes(getPageOrder(pageOrder));
        JSONArray responseJSON = getClientMemeJSON(allExisitingMemes);
        return new ResponseConstructors.ResponseBuilder()
                .setResponseCode(Constants.HTTP_FEATCH_SUCCESS)
                .setClientJSON(responseJSON)
                .build().getResponse();
    }

    private int getPageOrder(final String pageOrder) {
        if (IOCommonUtil.isValidString(pageOrder)) {
            try {
                int tempPageOrder = Integer.parseInt(pageOrder);
                if (tempPageOrder > 0) {
                    return tempPageOrder;
                }
            } catch (Exception exception) {

            }
        }
        return Constants.MINIMUM_PAGE_ORDER;
    }

    private JSONArray getClientMemeJSON(final List<Memes> allExisitingMemes) {
        JSONArray responseJSONArray = new JSONArray();
        if (IOCommonUtil.isValidList(allExisitingMemes)) {
            for (Memes allExisitingMeme : allExisitingMemes) {
                JSONObject singleMemeJSON = getClientJSON(allExisitingMeme);
                responseJSONArray.put(singleMemeJSON);
            }
        }
        return responseJSONArray;
    }

    public Response getSpecificMeme(String memeIDToFetch) {
        try {
            if (IOCommonUtil.isValidString(memeIDToFetch)) {
                long memeID = Long.parseLong(memeIDToFetch);
                if (IOCommonUtil.isValidLong(memeID)) {
                    Memes memeObjecFetch = new MemeManager().getExistingMeme(null, null, memeID);
                    if (IOCommonUtil.isValidObject(memeObjecFetch)) {
                        JSONObject responseJSON = getClientJSON(memeObjecFetch);
                        return new ResponseConstructors.ResponseBuilder()
                                .setResponseCode(Constants.HTTP_FEATCH_SUCCESS)
                                .setClientJSON(responseJSON)
                                .build()
                                .getResponse();
                    }
                }
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
        return ResponseConstructors.notFound();
    }

    private JSONObject getClientJSON(final Memes memeObjecFetch) {

        if (IOCommonUtil.isValidObject(memeObjecFetch)) {
            return memeObjecFetch.valueAsJSON();
        }
        return new JSONObject();
    }
}
