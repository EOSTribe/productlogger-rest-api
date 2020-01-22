package api.proxy.controller;

import api.proxy.service.ApiResponse;
import api.proxy.service.ApiService;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;


@RestController
@RequestMapping("/product")
public class ProductController {

    private static Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    private String API_SERVER;

    private static final String REQUEST = "{\n" +
            "  \"json\": true,\n" +
            "  \"code\": \"productloger\",\n" +
            "  \"scope\": \"productloger\",\n" +
            "  \"table\": \"product\",\n" +
            "  \"table_key\": \"\",\n" +
            "  \"lower_bound\": \"\",\n" +
            "  \"upper_bound\": \"\",\n" +
            "  \"limit\": 100,\n" +
            "  \"key_type\": \"\",\n" +
            "  \"index_position\": \"\",\n" +
            "  \"encode_type\": \"dec\",\n" +
            "  \"reverse\": false,\n" +
            "  \"show_payer\": false\n" +
            "}";


    @Autowired
    public void setProperties(Properties properties) {
        API_SERVER = properties.getApiServer();
    }

    @RequestMapping(value = "/{tag}", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Object> getProductByTag(@PathVariable(name="tag") String tag) {
        int tagInt;
        try {
            tagInt = Integer.parseInt(tag);
        } catch(NumberFormatException nfex) {
            return new ResponseEntity<>("Invalid tag id", HttpStatus.BAD_REQUEST);
        }
        try {
            ApiResponse response = ApiService.call(API_SERVER + "/v1/chain/get_table_rows", REQUEST);
            if (response.getCode() == 200) {
                String content = response.getContent();
                JSONObject contentJson = new JSONObject(content);
                JSONArray rowsJson = contentJson.getJSONArray("rows");
                String found = null;
                for (int i = 0; i < rowsJson.length(); i++) {
                    JSONObject jsonObject = rowsJson.getJSONObject(i);
                    int rowTag = jsonObject.getInt("tag");
                    if(tagInt == rowTag) {
                        found = jsonObject.toString();
                        break;
                    }
                }
                if(found != null) {
                    return new ResponseEntity<>(found, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
                }
            } else {
                LOGGER.error("Error making Chain API call: " + response.toString());
                return new ResponseEntity<>(response.getContent(), HttpStatus.valueOf(response.getCode()));
            }
        } catch (JSONException jsonex) {
            LOGGER.error("Error parsing JSON response : "+ jsonex.getMessage(), jsonex);
            return new ResponseEntity<>("{\"error\":\"JSON Parse error - "+jsonex.getMessage()+"\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            LOGGER.error("Error processing Chain API call : "+ex.getMessage(), ex);
            return new ResponseEntity<>("{\"error\":\"Other errors - " +ex.getMessage()+"\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
