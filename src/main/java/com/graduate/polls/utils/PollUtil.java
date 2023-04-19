package com.graduate.polls.utils;

import com.graduate.polls.controllers.PollController;
import com.graduate.polls.models.*;
import com.graduate.polls.models.dto.AnswerOptionDto;
import com.graduate.polls.models.dto.PollDto;
import com.graduate.polls.models.dto.QuestionDto;
import com.graduate.polls.models.dto.UserResponseDto;
import com.graduate.polls.service.api.PollService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.xmlbeans.impl.schema.StscChecker;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.*;

@Component
@RequiredArgsConstructor
public class PollUtil {

    private final PollService pollService;

    private Map<Long, Question> fillQuestionsMap(List<QuestionDto> questions, Poll parentPoll) {
        Map<Long, Question> questionsMap = new HashMap<>();

        for (var questionDto : questions) {
            if (questionsMap.containsKey(questionDto.getQuestionId())) {
                throw new IllegalArgumentException("Question id " + questionDto.getQuestionId() + " is duplicated");
            }
            Question question = new Question();
            question.setQuestionId(questionDto.getQuestionId());
            questionsMap.put(question.getQuestionId(), question);
        }

        for (var questionDto : questions) {
            Question question = questionsMap.get(questionDto.getQuestionId());
            question.setQuestionText(questionDto.getQuestionText());
            question.setPoll(parentPoll);

            List<AnswerOption> answerOptions = new ArrayList<>();

            for (var answerOptionDto : questionDto.getAnswers()) {
                AnswerOption answerOption = new AnswerOption();
                answerOption.setAnswerText(answerOptionDto.getAnswerText());
                answerOption.setQuestion(question);
                if (questionsMap.get(answerOptionDto.getNextQuestionId()) == null) {
                    throw new IllegalArgumentException("Question id " + answerOptionDto.getNextQuestionId() + " is not found");
                }
                answerOption.setNextQuestion(questionsMap.get(answerOptionDto.getNextQuestionId()));
                answerOptions.add(answerOption);
            }

            question.setAnswers(answerOptions);
        }

        return questionsMap;
    }

    private boolean checkQuestionsOrder(Map<Long, Integer> visitStatus, Question currQuestion) {
        visitStatus.put(currQuestion.getQuestionId(), 1);

        for (var answerOption : currQuestion.getAnswers()) {
            Question nextQuestion = answerOption.getNextQuestion();

            if (nextQuestion != currQuestion) {
                if (visitStatus.get(nextQuestion.getQuestionId()) == 1) {
                    return false;
                }
                if (visitStatus.get(nextQuestion.getQuestionId()) == 0) {
                    if (!checkQuestionsOrder(visitStatus, nextQuestion)) {
                        return false;
                    }
                }
            }
        }

        visitStatus.put(currQuestion.getQuestionId(), 2);

        return true;
    }

    private boolean checkUserResponse(UserResponse userResponse) {
        Poll poll = userResponse.getPoll();

        Question question = poll.getStartQuestion();

        int questionsCount = userResponse.getAnswers().size();

        while (questionsCount != 0) {
            final Question currQuestion = question;
            UserAnswer userAnswer = userResponse.getAnswers().stream()
                    .filter(answer -> answer.getQuestion().getQuestionId().equals(currQuestion.getQuestionId()))
                    .findFirst()
                    .orElse(null);

            if (userAnswer == null) {
                return false;
            }

            questionsCount--;

            question = userAnswer.getAnswerOption().getNextQuestion();

            if (questionsCount == 0) {
                return question == currQuestion;
            }

            if (question == currQuestion) {
                return false;
            }
        }

        return false;
    }

    public Poll convertToEntity(PollDto pollDto) {
        Poll poll = new Poll();
        poll.setApp((App) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        poll.setName(pollDto.getName());
        poll.setDescription(pollDto.getDescription());
        poll.setActive(true);
        poll.setUserId(pollDto.getUserId());
        Map <Long, Question> questionsMap = fillQuestionsMap(pollDto.getQuestions(), poll);
        if (questionsMap.get(pollDto.getStartQuestionId()) == null) {
            throw new IllegalArgumentException("Question id " + pollDto.getStartQuestionId() + " is not found");
        }
        poll.setStartQuestion(questionsMap.get(pollDto.getStartQuestionId()));
        poll.setQuestions(new ArrayList<>(questionsMap.values()));

        var visitStatus = new HashMap<Long, Integer>();

        for (var question : poll.getQuestions()) {
            visitStatus.put(question.getQuestionId(), 0);
        }

        var checkQuestionsOrder = checkQuestionsOrder(visitStatus, poll.getStartQuestion());

        if (!checkQuestionsOrder || visitStatus.containsValue(0)) {
            throw new IllegalArgumentException("Questions order have cycles or not connected");
        }

        return poll;
    }

    public Poll convertToEntity(Workbook workbook) throws MethodArgumentNotValidException, NoSuchMethodException {
        PollDto pollDto = readPoll(workbook);

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();

        Set<ConstraintViolation<PollDto>> violations = validator.validate(pollDto);
        if (!violations.isEmpty()) {
            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(pollDto, "pollDto");
            for (ConstraintViolation<PollDto> violation : violations) {
                bindingResult.addError(new FieldError("pollDto", violation.getPropertyPath().toString(), violation.getMessage()));
            }
            MethodParameter methodParameter = new MethodParameter(PollUtil.class.getMethod("convertToEntity", Workbook.class), 0);
            throw new MethodArgumentNotValidException(methodParameter, bindingResult);
        }

        return convertToEntity(pollDto);
    }

    private PollDto readPoll(Workbook workbook) {
        PollDto pollDto = new PollDto();
        try {
            Sheet sheet = workbook.getSheetAt(0);

            pollDto.setName(sheet.getRow(0).getCell(1).getStringCellValue());
            pollDto.setUserId(sheet.getRow(0).getCell(4).getStringCellValue());
            pollDto.setDescription(sheet.getRow(1).getCell(1).getStringCellValue());
            pollDto.setStartQuestionId((long) sheet.getRow(2).getCell(1).getNumericCellValue());

            int currRow = 5;

            List<QuestionDto> questions = new ArrayList<>();

            while (sheet.getRow(currRow) != null && sheet.getRow(currRow).getCell(0).getCellType() != CellType.BLANK) {
                Row row = sheet.getRow(currRow);

                QuestionDto questionDto = new QuestionDto();

                questionDto.setQuestionId((long) row.getCell(0).getNumericCellValue());
                questionDto.setQuestionText(row.getCell(1).getStringCellValue());

                List<AnswerOptionDto> answers = new ArrayList<>();

                AnswerOptionDto firstAnswer = new AnswerOptionDto();

                firstAnswer.setAnswerText(sheet.getRow(currRow).getCell(2).getStringCellValue());
                firstAnswer.setNextQuestionId((long) sheet.getRow(currRow).getCell(3).getNumericCellValue());

                answers.add(firstAnswer);

                int answerRow = currRow + 1;
                while (sheet.getRow(answerRow) != null && sheet.getRow(answerRow).getCell(0).getCellType() == CellType.BLANK && sheet.getRow(answerRow).getCell(2).getCellType() != CellType.BLANK) {
                    AnswerOptionDto answerOptionDto = new AnswerOptionDto();

                    answerOptionDto.setAnswerText(sheet.getRow(answerRow).getCell(2).getStringCellValue());
                    answerOptionDto.setNextQuestionId((long) sheet.getRow(answerRow).getCell(3).getNumericCellValue());

                    answers.add(answerOptionDto);

                    answerRow++;
                }

                questionDto.setAnswers(answers);
                questions.add(questionDto);

                currRow = answerRow;
            }

            pollDto.setQuestions(questions);
        } catch (Exception e) {
            throw new IllegalArgumentException("Excel file is not valid!");
        }

        return pollDto;
    }

    public UserResponse convertToEntity(UserResponseDto userResponseDto, Long pollId) throws Exception {
        Poll poll = pollService.getPoll(pollId);

        UserResponse userResponse = new UserResponse();
        userResponse.setPoll(poll);
        userResponse.setApp(poll.getApp());
        userResponse.setUserId(userResponseDto.getUserId());

        List<UserAnswer> userAnswers = new ArrayList<>();
        for (var userAnswerDto : userResponseDto.getAnswers()) {
            Question question = pollService.getQuestion(pollId, userAnswerDto.getQuestionId());

            UserAnswer userAnswer = new UserAnswer();
            userAnswer.setQuestion(question);
            userAnswer.setUserResponse(userResponse);
            userAnswer.setAnswerOption(question.getAnswers().stream()
                    .filter(answerOption -> answerOption.getId().equals(userAnswerDto.getAnswerId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Answer " + userAnswerDto.getAnswerId() + " is not found")));

            userAnswers.add(userAnswer);
        }

        userResponse.setAnswers(userAnswers);

        if (!checkUserResponse(userResponse)) {
            throw new IllegalArgumentException("User response is not valid");
        }

        return userResponse;
    }
}
