package com.bogdanenache.Code_Sharing_Platform.controllers;

import com.bogdanenache.Code_Sharing_Platform.entities.Data;
import com.bogdanenache.Code_Sharing_Platform.repositories.DataRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

@RestController
public class AppController {

    @Autowired
    private DataRepository dataRepository;

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
        data.setCodeID("" + (dataRepository.count() + 1));
        dataRepository.save(data);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", data.getCodeID());

        return jsonObject.toString();
    }

    @GetMapping(value = "/api/code/{id}")
    public Data getJsonData(HttpServletResponse response, @PathVariable String id) {
        response.addHeader("Content-Type", "application/json");

        return dataRepository.findByCodeID(id);
    }

    @GetMapping(value = "/api/code/latest")
    public ArrayList<Data> getLatestData() {
        ArrayList<Data> latestData = new ArrayList<>();

        if (dataRepository.count() < 10) {
            for (long i = dataRepository.count(); i > 0; i--) {
                latestData.add(dataRepository.findByCodeID("" + i));
            }
        } else {
            long start = dataRepository.count() - 10;

            for (long i = dataRepository.count(); i > start; i--) {
                latestData.add(dataRepository.findByCodeID("" + i));
            }
        }
        return latestData;
    }


    @GetMapping(value = "/code/{id}")
    public ModelAndView getHtml(HttpServletResponse response, @PathVariable int id) {
        response.addHeader("Content-Type", "text/html");

        Data data = dataRepository.findByCodeID("" + id);

        String codeDate = data.getDate();
        String codeBody = data.getCode();

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

        if (dataRepository.count() < 10) {
            for (long i = dataRepository.count(); i > 0; i--) {
                latestData.add(dataRepository.findByCodeID("" + i));
            }
        } else {
            long start = dataRepository.count() - 10;

            for (long i = dataRepository.count(); i > start; i--) {
                latestData.add(dataRepository.findByCodeID("" + i));
            }
        }

        model.addObject("latestData", latestData);
        model.setViewName("latestCodes");
        return model;
    }

}
