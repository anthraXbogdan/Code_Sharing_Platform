package com.bogdanenache.Code_Sharing_Platform.controllers;

import com.bogdanenache.Code_Sharing_Platform.entities.Data;
import com.bogdanenache.Code_Sharing_Platform.entities.EmptyClass;
import com.bogdanenache.Code_Sharing_Platform.repositories.DataRepository;
import javassist.NotFoundException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
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
    public  String postJsonData(@RequestBody Data data) throws JSONException, InterruptedException {
        data.setTime2(data.getTime());
        data.setDate(getDateTimeStamp());
        data.setCodeID("" + (dataRepository.count() + 1));
        data.setUuid(createUuid());
        data.setStartTime(Instant.now());
        data.setTimeRestricted(true);
        data.setViewsRestricted(true);

        dataRepository.save(data);

        if (data.getTime() <= 0 && data.getViews() <= 0) {
            data.setTimeRestricted(false);
            data.setViewsRestricted(false);
            dataRepository.save(data);
        } else if (data.getTime() <= 0 && data.getViews() > 0) {
            data.setTimeRestricted(false);
            data.setViewsRestricted(true);
            dataRepository.save(data);
        } else if (data.getTime() > 0 && data.getViews() <= 0) {
            data.setTimeRestricted(true);
            data.setViewsRestricted(false);
            dataRepository.save(data);
        }

        data.setTime2(data.getTime());
        dataRepository.save(data);

        Thread.sleep(100);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", data.getUuid());

        return jsonObject.toString();
    }

    @GetMapping(value = "/api/code/{uuid}")
    public String getJsonData(HttpServletResponse response, @PathVariable String uuid) throws JSONException {
        response.addHeader("Content-Type", "application/json");

        Data data = dataRepository.findByUuid(uuid);

        if (data == null) {
            response.setStatus(404);
            return null;
        } else {
            Duration duration = Duration.between(data.getStartTime(), Instant.now());
            long x = duration.toSeconds();
            int remainingViews = data.getViews() - 1;
            long timeAmount = data.getTime2();

            if (data.isTimeRestricted() && data.isViewsRestricted()) {
                data.setViews(remainingViews);
                data.setTime(timeAmount - x);
                dataRepository.save(data);
            }
            else if (data.isTimeRestricted() && !data.isViewsRestricted()){
                data.setTime(timeAmount - x);
                dataRepository.save(data);
            }
            else if (!data.isTimeRestricted() && data.isViewsRestricted()) {
                data.setViews(remainingViews);
                dataRepository.save(data);
            }

            JSONObject jsonObject = new JSONObject();

            if (data.getViews() < 0 || data.getTime() < 0) {
                dataRepository.delete(data);
                response.setStatus(404);

                return null;
            }
            else {
                jsonObject.put("code", data.getCode());
                jsonObject.put("date", data.getDate());
                jsonObject.put("time", data.getTime());
                jsonObject.put("views", data.getViews());

                return jsonObject.toString();
            }
        }
    }

    @GetMapping(value = "/api/code/latest")
    public ArrayList<EmptyClass> getLatestData() {
        ArrayList<EmptyClass> latestData = new ArrayList<>();
        ArrayList<EmptyClass> latest10Data = new ArrayList<>();

        if (dataRepository.count() < 10) {
            for (long i = dataRepository.count(); i > 0; i--) {
                Data data = dataRepository.findByCodeID("" + i);

                if (data != null) {
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
        } else {
            for (long i = dataRepository.count(); i > 0; i--) {
                Data data = dataRepository.findByCodeID("" + i);

                if (data != null) {
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
            for (int i = 0; i < 10; i++) {
                latest10Data.add(latestData.get(i));
            }
            return latest10Data;
        }
    }

    @GetMapping(value = "/code/{uuid}")
    public ModelAndView getHtml(HttpServletResponse response, @PathVariable String uuid) throws NotFoundException {
        response.addHeader("Content-Type", "text/html");

        Data data = dataRepository.findByUuid(uuid);

        if (data == null) {
            response.setStatus(404);
            return null;
        } else {
            Duration duration;
            duration = Duration.between(data.getStartTime(), Instant.now());
            assert duration != null;
            long x = duration.toSeconds();
            int remainingViews = data.getViews() - 1;
            long timeAmount = data.getTime2();

            if (data.isTimeRestricted() && data.isViewsRestricted()) {
                data.setViews(remainingViews);
                data.setTime(timeAmount - x);
                dataRepository.save(data);
            }
            else if (data.isTimeRestricted() && !data.isViewsRestricted()){
                data.setTime(timeAmount - x);
                dataRepository.save(data);
            }
            else if (!data.isTimeRestricted() && data.isViewsRestricted()) {
                data.setViews(remainingViews);
                dataRepository.save(data);
            }

            ModelAndView model = new ModelAndView();

            if (data.getViews() < 0 || data.getTime() < 0) {
                dataRepository.delete(data);
                response.setStatus(404);

                return null;
            }
            else {
                String codeDate = data.getDate();
                String codeBody = data.getCode();
                String viewsNo = "" + data.getViews() + " more views allowed";
                String time = "The code will be available for " + data.getTime() + " seconds";
                String replaced = codeBody.replaceAll("<", "&lt");

                if (data.isViewsRestricted() && data.isTimeRestricted()) {
                    model.addObject("time", time);
                    model.addObject("viewsNo", viewsNo);
                    model.addObject("dateTimeStamp", codeDate);
                    model.addObject("codeBody", replaced);
                    model.setViewName("code");
                }
                else if (!data.isViewsRestricted() && !data.isTimeRestricted()) {
                    model.addObject("dateTimeStamp", codeDate);
                    model.addObject("codeBody", replaced);
                    model.setViewName("noRestrictions");
                }
                else if (!data.isViewsRestricted() && data.isTimeRestricted()) {
                    model.addObject("time", time);
                    model.addObject("dateTimeStamp", codeDate);
                    model.addObject("codeBody", replaced);
                    model.setViewName("withTimeRestriction");
                }
                else if (data.isViewsRestricted() && !data.isTimeRestricted()) {
                    model.addObject("viewsNo", viewsNo);
                    model.addObject("dateTimeStamp", codeDate);
                    model.addObject("codeBody", replaced);
                    model.setViewName("withViewsRestriction");
                }
                return model;
            }
        }
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

        ArrayList<EmptyClass> latestData = new ArrayList<>();
        ArrayList<EmptyClass> latest10Data = new ArrayList<>();

        if (dataRepository.count() < 10) {
            for (long i = dataRepository.count(); i > 0; i--) {
                Data data = dataRepository.findByCodeID("" + i);

                if (data != null) {
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
            model.addObject("latestData", latestData);
        } else {
            for (long i = dataRepository.count(); i > 0; i--) {
                Data data = dataRepository.findByCodeID("" + i);

                if (data != null) {
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
            for (int i = 0; i < 10; i++) {
                latest10Data.add(latestData.get(i));
            }
            model.addObject("latestData", latest10Data);
        }
        model.setViewName("latestCodes");
        return model;
    }

}
