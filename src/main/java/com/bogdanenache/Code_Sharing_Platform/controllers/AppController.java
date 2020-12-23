package com.bogdanenache.Code_Sharing_Platform.controllers;

import com.bogdanenache.Code_Sharing_Platform.entities.Data;
import com.bogdanenache.Code_Sharing_Platform.entities.EmptyClass;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class AppController {

    private final Data[] dataList = new Data[1];

    public AppController() {
        Data data = new Data();
        data.setCode("plm");
        data.setDate(getDateTimeStamp());
        dataList[0] = data;
    }

    public String getDateTimeStamp() {
        LocalDateTime dateTime = LocalDateTime.now();
        String DATE_FORMATTER= "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);

        return dateTime.format(formatter);
    }

    @PostMapping(value = "/api/code/new", consumes = "application/json")
    public EmptyClass postJsonData(@RequestBody Data data, EmptyClass emptyClass) {
        data.setDate(getDateTimeStamp());
        dataList[0] = data;

        return emptyClass;
    }

    @GetMapping(value = "/api/code")
    public Data getJsonData(HttpServletResponse response) {
        response.addHeader("Content-Type", "application/json");

        return dataList[0];
    }

    @GetMapping(value = "/code")
    public ModelAndView getHtml(HttpServletResponse response) {
        response.addHeader("Content-Type", "text/html");

        String codeBody = dataList[0].getCode();
        String codeDate = dataList[0].getDate();

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

}
