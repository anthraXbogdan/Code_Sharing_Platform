package com.bogdanenache.Code_Sharing_Platform.controllers;

import com.bogdanenache.Code_Sharing_Platform.entities.Data;
import com.bogdanenache.Code_Sharing_Platform.entities.EmptyClass;
import com.bogdanenache.Code_Sharing_Platform.repositories.DataRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;

@RestController
public class AppController {

    @Autowired
    private DataRepository dataRepository;

    public AppController() {
    }

    public String createUuid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
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
        data.setUuid(createUuid());
        data.setStartTime(Instant.now());
        data.setTime2(data.getTime());
        data.setSecret(true);

        dataRepository.save(data);

        if (data.getTime() <= 0 && data.getViews() <= 0 && data.isSecret()) {
            data.setSecret(false);
            dataRepository.save(data);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", data.getUuid());

        return jsonObject.toString();
    }

    @GetMapping(value = "/api/code/{uuid}")
    public String getJsonData(HttpServletResponse response, @PathVariable String uuid) throws JSONException {
        response.addHeader("Content-Type", "application/json");

        Data data = dataRepository.findByUuid(uuid);

        Duration duration = Duration.between(data.getStartTime(), Instant.now());
        long x = duration.toSeconds();
        int remainingViews = data.getViews() - 1;
        long timeAmount = data.getTime2();

        if (data.isSecret()) {
            data.setViews(remainingViews);
            data.setTime(timeAmount - x);
            dataRepository.save(data);
        }

        JSONObject jsonObject = new JSONObject();

        if (data.getViews() < 0 || data.getTime() < 0 && data.isSecret()) {
            dataRepository.delete(data);
            response.setStatus(404);

            return null;
        }
        else if (data.getViews() >= 0 || data.getTime() >= 0 && data.isSecret()) {
            jsonObject.put("code", data.getCode());
            jsonObject.put("date", data.getDate());
            jsonObject.put("time", data.getTime());
            jsonObject.put("views", data.getViews());

            return jsonObject.toString();
        }
        else if (data.getViews() <= 0 && data.getTime() <= 0 && !data.isSecret()) {
            jsonObject.put("code", data.getCode());
            jsonObject.put("date", data.getDate());
            jsonObject.put("time", data.getTime());
            jsonObject.put("views", data.getViews());

            return jsonObject.toString();
        }
        return jsonObject.toString();
    }

    @GetMapping(value = "/api/code/latest")
    public ArrayList<EmptyClass> getLatestData() {
        ArrayList<EmptyClass> latestData = new ArrayList<>();

        if (dataRepository.count() < 10) {
            for (long i = dataRepository.count(); i > 0; i--) {
                Data data = dataRepository.findByCodeID("" + i);

                if (data.getTime() <= 0 && data.getViews() <= 0) {
                    EmptyClass emptyClass = new EmptyClass();

                    emptyClass.setCode(data.getCode());
                    emptyClass.setDate(data.getDate());
                    emptyClass.setTime(data.getTime());
                    emptyClass.setViews(data.getViews());

                    latestData.add(emptyClass);
                }
            }
        } else {
            long start = dataRepository.count() - 10;

            for (long i = dataRepository.count(); i > start; i--) {
                Data data = dataRepository.findByCodeID("" + i);

                if (data.getTime() <= 0 && data.getViews() <= 0) {
                    EmptyClass emptyClass = new EmptyClass();

                    emptyClass.setCode(data.getCode());
                    emptyClass.setDate(data.getDate());
                    emptyClass.setTime(data.getTime());
                    emptyClass.setViews(data.getViews());

                    latestData.add(emptyClass);
                }
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
        String replaced = codeBody.replaceAll("<", "&lt");

        ModelAndView model = new ModelAndView();

        model.addObject("dateTimeStamp", codeDate);
        model.addObject("codeBody", replaced);
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
