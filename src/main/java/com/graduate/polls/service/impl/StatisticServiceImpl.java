package com.graduate.polls.service.impl;

import com.graduate.polls.models.*;
import com.graduate.polls.repository.UserResponseRepository;
import com.graduate.polls.service.api.PollService;
import com.graduate.polls.service.api.ResponseService;
import com.graduate.polls.service.api.StatisticService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
    private final UserResponseRepository userResponseRepository;
    private final ResponseService responseService;

    private final PollService pollService;

    @Override
    public PollStatistic getPollStatistics(Long pollId) throws Exception {
        pollService.getPoll(pollId);
        PollStatistic pollStatistic = new PollStatistic();
        pollStatistic.setTotalResponses(userResponseRepository.countByPollId(pollId));

        List<QuestionStatistic> questionStatistics = new ArrayList<>();

        for (var question : userResponseRepository.countQuestionResponses(pollId)) {
            QuestionStatistic questionStatistic = new QuestionStatistic();

            questionStatistic.setQuestionId(question.get("question_id", Long.class));
            questionStatistic.setQuestionText(question.get("question_text", String.class));
            questionStatistic.setTotalResponses(question.get("response_number", Long.class));
            questionStatistic.setPercentage(questionStatistic.getTotalResponses() / (double) pollStatistic.getTotalResponses() * 100);

            List<AnswerStatistic> answerStatistics = new ArrayList<>();

            for (var answer : userResponseRepository.countAnswerResponses(pollId, question.get("id", Long.class))) {
                AnswerStatistic answerStatistic = new AnswerStatistic();

                answerStatistic.setAnswerId(answer.get("id", Long.class));
                answerStatistic.setAnswerText(answer.get("answer_text", String.class));
                answerStatistic.setTotalResponses(answer.get("response_number", Long.class));
                answerStatistic.setPercentage(answerStatistic.getTotalResponses() / (double) questionStatistic.getTotalResponses() * 100);

                answerStatistics.add(answerStatistic);
            }

            questionStatistic.setAnswers(answerStatistics);
            questionStatistics.add(questionStatistic);
        }

        pollStatistic.setQuestions(questionStatistics);

        return pollStatistic;
    }

    @Override
    public byte[] createPollReport(Long pollId) throws Exception {
        Poll poll = pollService.getPoll(pollId);
        PollStatistic pollStatistic = getPollStatistics(pollId);

        Workbook workbook = new XSSFWorkbook();
        createPollSheet(workbook, poll, pollStatistic);
        createQuestionSheets(workbook, poll, pollStatistic);
        createResponseSheet(workbook, poll);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    private void createPollSheet(Workbook workbook, Poll poll, PollStatistic pollStatistic) {
        Sheet sheet = workbook.createSheet("Опрос");

        Font font = workbook.createFont();
        font.setBold(true);
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);

        Cell idCell = sheet.createRow(0).createCell(0);
        idCell.setCellValue("ID");
        idCell.setCellStyle(style);
        sheet.getRow(0).createCell(1).setCellValue(poll.getId());

        Cell titleCell = sheet.createRow(1).createCell(0);
        titleCell.setCellValue("Название");
        titleCell.setCellStyle(style);
        sheet.getRow(1).createCell(1).setCellValue(poll.getName());

        Cell responseNumberCell = sheet.createRow(3).createCell(0);
        responseNumberCell.setCellValue("Количество ответов");
        responseNumberCell.setCellStyle(style);
        sheet.getRow(3).createCell(1).setCellValue(pollStatistic.getTotalResponses());

        sheet.autoSizeColumn(0);
    }

    private void createQuestionSheets(Workbook workbook, Poll poll, PollStatistic pollStatistic) {
        for (QuestionStatistic questionStatistic : pollStatistic.getQuestions()) {
            Sheet sheet = workbook.createSheet("Вопрос " + questionStatistic.getQuestionId());

            Font font = workbook.createFont();
            font.setBold(true);
            CellStyle style = workbook.createCellStyle();
            style.setFont(font);
            style.setAlignment(HorizontalAlignment.CENTER);

            Cell idCell = sheet.createRow(0).createCell(0);
            idCell.setCellValue("ID");
            idCell.setCellStyle(style);
            sheet.getRow(0).createCell(1).setCellValue(questionStatistic.getQuestionId());

            Cell titleCell = sheet.createRow(1).createCell(0);
            titleCell.setCellValue("Текст вопроса");
            titleCell.setCellStyle(style);
            sheet.getRow(1).createCell(1).setCellValue(questionStatistic.getQuestionText());

            Cell responseNumberCell = sheet.createRow(2).createCell(0);
            responseNumberCell.setCellValue("Количество ответов");
            responseNumberCell.setCellStyle(style);
            sheet.getRow(2).createCell(1).setCellValue(questionStatistic.getTotalResponses());

            Cell percentageCell = sheet.createRow(3).createCell(0);
            percentageCell.setCellValue("Процент ответов");
            percentageCell.setCellStyle(style);
            sheet.getRow(3).createCell(1).setCellValue(questionStatistic.getPercentage());

            sheet.getRow(1).createCell(2).setCellValue("Количество ответов");
            sheet.getRow(1).getCell(2).setCellStyle(style);

            sheet.getRow(2).createCell(2).setCellValue("Распределение в %");
            sheet.getRow(2).getCell(2).setCellStyle(style);

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);

            int i = 3;
            for (AnswerStatistic answerStatistic : questionStatistic.getAnswers()) {
                Row answerRow = sheet.getRow(0);
                Row responseRow = sheet.getRow(1);
                Row percentageRow = sheet.getRow(2);

                answerRow.createCell(i).setCellValue(answerStatistic.getAnswerText());
                answerRow.getCell(i).setCellStyle(style);

                responseRow.createCell(i).setCellValue(answerStatistic.getTotalResponses());
                percentageRow.createCell(i).setCellValue(answerStatistic.getPercentage());
                sheet.autoSizeColumn(i);

                i++;
            }
        }
    }

    private void createResponseSheet(Workbook workbook, Poll poll) {
        Sheet sheet = workbook.createSheet("Ответы пользователей");

        Font font = workbook.createFont();
        font.setBold(true);
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID ответа");
        headerRow.getCell(0).setCellStyle(style);
        headerRow.createCell(1).setCellValue("ID пользователя");
        headerRow.getCell(1).setCellStyle(style);
        headerRow.createCell(2).setCellValue("Дата ответа");
        headerRow.getCell(2).setCellStyle(style);

        int i = 3;

        for (var question : poll.getQuestions()) {
            headerRow.createCell(i).setCellValue("Вопрос " + question.getQuestionId());
            headerRow.getCell(i).setCellStyle(style);
            i += 1;
        }

        i = 1;
        for (var response : poll.getUserResponses()) {
            Row responseRow = sheet.createRow(i);
            responseRow.createCell(0).setCellValue(response.getId());
            responseRow.createCell(1).setCellValue(response.getUserId());
            responseRow.createCell(2).setCellValue(response.getCreatedAt().toString());

            int j = 3;
            for (var question : poll.getQuestions()) {
                final int finalJ = j;
                response.getAnswers().stream()
                        .filter(answer -> answer.getQuestion().getQuestionId().equals(question.getQuestionId()))
                        .findFirst()
                        .ifPresent(
                                answer -> responseRow.createCell(finalJ).setCellValue(answer.getAnswerOption().getAnswerText())
                        );
                j++;
            }
            i++;
        }

        for (int j = 0; j < poll.getQuestions().size() + 3; j++) {
            sheet.autoSizeColumn(j);
        }
    }
}
