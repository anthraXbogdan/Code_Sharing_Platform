package com.bogdanenache.Code_Sharing_Platform.controllers;

import com.bogdanenache.Code_Sharing_Platform.entities.Data;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@RestController
public class AppController {
    private final ArrayList<Data> dataList = new ArrayList<>();

    public AppController() {
    }

    public String getDateTimeStamp() {
        LocalDateTime dateTime = LocalDateTime.now();
        String DATE_FORMATTER= "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);

        return dateTime.format(formatter);
    }

    @PostMapping(value = "/api/code/new", consumes = "application/json")
    public  String postJsonData(@RequestBody Data data) throws JSONException {
        data.setDate(getDateTimeStamp());
        data.setId(dataList.size() + 1);
        dataList.add(data);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", data.getId());

        return jsonObject.toString();
    }

    @GetMapping(value = "/api/code/{id}")
    public Data getJsonData(HttpServletResponse response,  @PathVariable int id) {
        response.addHeader("Content-Type", "application/json");

        return dataList.get(id - 1);
    }

    @GetMapping(value = "/api/code/latest")
    public ArrayList<Data> getLatestData() {
        ArrayList<Data> latestData = new ArrayList<>();

        for (int i = dataList.size() -1; i >= 0; i--) {
            latestData.add(dataList.get(i));
        }

        ArrayList<Data> latestTenData = new ArrayList<>();

        if (latestData.size() >= 10) {
            for (int i = 0 ; i < 10; i++) {
                latestTenData.add(latestData.get(i));
            }
            return latestTenData;
        }
        return latestData;
    }


    @GetMapping(value = "/code/{id}")
    public ModelAndView getHtml(HttpServletResponse response, @PathVariable int id) {
        response.addHeader("Content-Type", "text/html");

        String codeDate = dataList.get(id - 1).getDate();
        String codeBody = dataList.get(id - 1).getCode();

        ModelAndView model = new ModelAndView();

        model.addObject("dateTimeStamp", codeDate);
        model.addObject("codeBody", codeBody);
        model.setViewName("code");

        return model;
    }

    @GetMapping(value = "/code/new")
    public ModelAndView newCode(HttpServletResponse response) {
        response.addHeader("Content-Type", "text/html");

        ModelAndView model = new ModelAndView();

        model.setViewName("createCode");
        return model;
    }

    @GetMapping(value = "/code/latest")
    public ModelAndView latestCode() {
        ModelAndView model = new ModelAndView();

        ArrayList<Data> latestData = new ArrayList<>();

        for (int i = dataList.size() -1; i >= 0; i--) {
            latestData.add(dataList.get(i));
        }

        ArrayList<Data> latestTenData = new ArrayList<>();

        if (latestData.size() >= 10) {
            for (int i = 0 ; i < 10; i++) {
                latestTenData.add(latestData.get(i));
            }
            model.addObject("latestTenData", latestTenData);
        } else {
            latestTenData.addAll(latestData);
            model.addObject("latestTenData", latestTenData);
        }

        model.setViewName("latestCodes");
        return model;
    }

}
