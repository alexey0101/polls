package com.graduate.polls.service.impl;

import com.graduate.polls.models.*;
import com.graduate.polls.repository.UserResponseRepository;
import com.graduate.polls.service.api.PollService;
import com.graduate.polls.service.api.StatisticService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
    private final UserResponseRepository userResponseRepository;
    private final PollService pollService;

    /**
     * Get poll statistics
     * @param pollId
     * @return
     * @throws Exception
     */
    @Override
    public PollStatistic getPollStatistics(Long pollId) throws Exception {
        Poll poll = pollService.getPoll(pollId);
        PollStatistic pollStatistic = new PollStatistic();
        pollStatistic.setTotalResponses(userResponseRepository.countByPollId(pollId));

        List<QuestionStatistic> questionStatistics = new ArrayList<>();

        for (var question : userResponseRepository.calcAnswerStatistic(pollId)) {
            QuestionStatistic questionStatistic = new QuestionStatistic();
            Question questionEntity = pollService.getQuestion(question.get("question_id", Long.class));

            questionStatistic.setQuestionType(questionEntity.getQuestionType());
            questionStatistic.setQuestionId(questionEntity.getQuestionId());
            questionStatistic.setQuestionText(questionEntity.getQuestionText());

            if (questionEntity.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
                questionStatistic.setTotalResponses(userResponseRepository.countMultipleChoiceResponsesByResponseIdAndQuestionId(pollId, questionEntity.getId()));
            } else {
                questionStatistic.setTotalResponses(question.get("response_number", Long.class));
            }

            if (questionEntity.getQuestionType() == QuestionType.SCALE) {
                questionStatistic.setScaleMean(question.get("scale_mean", BigDecimal.class));
                questionStatistic.setScaleMode(question.get("scale_mode", Long.class));
                questionStatistic.setScaleMedian(question.get("scale_median", Double.class));
                questionStatistic.setScaleStd(question.get("scale_std", BigDecimal.class));
            }

            questionStatistic.setPercentage(questionStatistic.getTotalResponses() / (double) pollStatistic.getTotalResponses() * 100);

            List<AnswerStatistic> answerStatistics = new ArrayList<>();

            for (var answer : userResponseRepository.calcAnswerOptionStatistic(pollId, questionEntity.getId())) {
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

    /**
     * Create poll report
     * @param pollId
     * @return
     * @throws Exception
     */
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

    /**
     * Create poll xlsx sheet
     * @param workbook
     * @param poll
     * @param pollStatistic
     */
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

    /**
     * Create question xlsx sheet
     * @param workbook
     * @param poll
     */
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

            Cell typeCell = sheet.createRow(1).createCell(0);
            typeCell.setCellValue("Тип вопроса");
            typeCell.setCellStyle(style);
            if (questionStatistic.getQuestionType().equals(QuestionType.MULTIPLE_CHOICE)) {
                sheet.getRow(1).createCell(1).setCellValue("Множественный выбор");
            } else if (questionStatistic.getQuestionType().equals(QuestionType.SINGLE_CHOICE)) {
                sheet.getRow(1).createCell(1).setCellValue("Единственный выбор");
            } else if (questionStatistic.getQuestionType().equals(QuestionType.SCALE)) {
                sheet.getRow(1).createCell(1).setCellValue("Шкала");
            } else {
                sheet.getRow(1).createCell(1).setCellValue("Открытый");
            }

            Cell titleCell = sheet.createRow(2).createCell(0);
            titleCell.setCellValue("Текст вопроса");
            titleCell.setCellStyle(style);
            sheet.getRow(2).createCell(1).setCellValue(questionStatistic.getQuestionText());

            Cell responseNumberCell = sheet.createRow(3).createCell(0);
            responseNumberCell.setCellValue("Количество ответов");
            responseNumberCell.setCellStyle(style);
            sheet.getRow(3).createCell(1).setCellValue(questionStatistic.getTotalResponses());

            Cell percentageCell = sheet.createRow(4).createCell(0);
            percentageCell.setCellValue("Процент ответов");
            percentageCell.setCellStyle(style);
            sheet.getRow(4).createCell(1).setCellValue(questionStatistic.getPercentage());

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            if (questionStatistic.getQuestionType().equals(QuestionType.MULTIPLE_CHOICE) ||
                    questionStatistic.getQuestionType().equals(QuestionType.SINGLE_CHOICE)) {
                sheet.createRow(7).createCell(0).setCellValue("Количество ответов");
                sheet.getRow(7).getCell(0).setCellStyle(style);

                sheet.createRow(8).createCell(0).setCellValue("Распределение в %");
                sheet.getRow(8).getCell(0).setCellStyle(style);

                sheet.createRow(6);

                int i = 1;
                for (AnswerStatistic answerStatistic : questionStatistic.getAnswers()) {
                    sheet.getRow(6).createCell(i).setCellValue(answerStatistic.getAnswerText());
                    sheet.getRow(6).getCell(i).setCellStyle(style);

                    sheet.getRow(7).createCell(i).setCellValue(answerStatistic.getTotalResponses());
                    sheet.getRow(8).createCell(i).setCellValue(answerStatistic.getPercentage());

                    sheet.autoSizeColumn(i);
                    i++;
                }

            } else if (questionStatistic.getQuestionType().equals(QuestionType.SCALE)) {
                sheet.createRow(6).createCell(0).setCellValue("Среднее значение");
                sheet.getRow(6).getCell(0).setCellStyle(style);
                sheet.getRow(6).createCell(1).setCellValue(questionStatistic.getScaleMean().doubleValue());

                sheet.createRow(7).createCell(0).setCellValue("Мода");
                sheet.getRow(7).getCell(0).setCellStyle(style);
                sheet.getRow(7).createCell(1).setCellValue(questionStatistic.getScaleMode().doubleValue());

                sheet.createRow(8).createCell(0).setCellValue("Медиана");
                sheet.getRow(8).getCell(0).setCellStyle(style);
                sheet.getRow(8).createCell(1).setCellValue(questionStatistic.getScaleMedian());

                sheet.createRow(9).createCell(0).setCellValue("Стандартное отклонение");
                if (questionStatistic.getScaleStd() != null) {
                    sheet.getRow(9).getCell(0).setCellStyle(style);
                    sheet.getRow(9).createCell(1).setCellValue(questionStatistic.getScaleStd().doubleValue());
                }

                sheet.autoSizeColumn(0);
                sheet.autoSizeColumn(1);
            }
        }
    }

    /**
     * Creates sheet with responses of users
     * @param workbook
     * @param poll
     */
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
                if (!question.getQuestionType().equals(QuestionType.MULTIPLE_CHOICE)) {
                    UserAnswer userAnswer = response.getAnswers().stream()
                            .filter(answer -> answer.getQuestion().getQuestionId().equals(question.getQuestionId()))
                            .findFirst().get();

                    if (userAnswer.getQuestion().getQuestionType().equals(QuestionType.SCALE)) {
                        responseRow.createCell(finalJ).setCellValue(userAnswer.getScaleValue());
                    } else if (userAnswer.getQuestion().getQuestionType().equals(QuestionType.TEXT)){
                        responseRow.createCell(finalJ).setCellValue(userAnswer.getAnswerText());
                    } else {
                        responseRow.createCell(finalJ).setCellValue(userAnswer.getAnswerOption().getAnswerText());
                    }
                } else {
                    List<UserAnswer> userAnswer = response.getAnswers().stream()
                            .filter(answer -> answer.getQuestion().getQuestionId().equals(question.getQuestionId())).toList();
                    responseRow.createCell(finalJ).setCellValue(userAnswer.stream().map(answer -> answer.getAnswerOption().getAnswerText()).collect(Collectors.joining(";")));
                }
                j++;
            }
            i++;
        }

        for (int j = 0; j < poll.getQuestions().size() + 3; j++) {
            sheet.autoSizeColumn(j);
        }
    }
}
