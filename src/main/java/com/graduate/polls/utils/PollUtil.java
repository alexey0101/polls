package com.graduate.polls.utils;

import com.graduate.polls.models.*;
import com.graduate.polls.models.dto.AnswerOptionDto;
import com.graduate.polls.models.dto.PollDto;
import com.graduate.polls.models.dto.QuestionDto;
import com.graduate.polls.models.dto.UserResponseDto;
import com.graduate.polls.service.api.PollService;
import com.graduate.polls.service.api.TagService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.*;

@Component
@RequiredArgsConstructor
public class PollUtil {
    private final PollService pollService;

    private final TagService tagService;

    /**
     * Fill and validate poll questions from dto
     * @param questions
     * @param parentPoll
     * @return
     */
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
            question.setQuestionType(questionDto.getQuestionType());

            if (questionDto.getQuestionType() == QuestionType.SINGLE_CHOICE) {
                if (questionDto.getNextQuestionId() != null) {
                    throw new IllegalArgumentException("Next question id " + questionDto.getNextQuestionId() + " must be null for single choice questions");
                }
                if (questionDto.getScaleMin() != null || questionDto.getScaleMax() != null) {
                    throw new IllegalArgumentException("Scale min and max must be null for single choice questions");
                }
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

            if (questionDto.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
                if (questionDto.getNextQuestionId() == null) {
                    throw new IllegalArgumentException("Next question id " + questionDto.getNextQuestionId() + " must not be null for multiple choice questions");
                }
                if (questionDto.getScaleMin() != null || questionDto.getScaleMax() != null) {
                    throw new IllegalArgumentException("Scale min and max must be null for multiple choice questions");
                }
                List<AnswerOption> answerOptions = new ArrayList<>();
                for (var answerOptionDto : questionDto.getAnswers()) {
                    AnswerOption answerOption = new AnswerOption();
                    answerOption.setAnswerText(answerOptionDto.getAnswerText());
                    answerOption.setQuestion(question);
                    answerOptions.add(answerOption);
                }
                question.setAnswers(answerOptions);
                if (questionsMap.get(questionDto.getNextQuestionId()) == null) {
                    throw new IllegalArgumentException("Question id " + questionDto.getNextQuestionId() + " is not found");
                }
                question.setNextQuestion(questionsMap.get(questionDto.getNextQuestionId()));
            }

            if (questionDto.getQuestionType() == QuestionType.SCALE) {
                if (questionDto.getNextQuestionId() == null) {
                    throw new IllegalArgumentException("Next question id must be specified for question id " + questionDto.getQuestionId());
                }
                if (questionDto.getScaleMin() == null || questionDto.getScaleMax() == null) {
                    throw new IllegalArgumentException("Scale min and max must be specified for question id " + questionDto.getQuestionId());
                }
                if (questionDto.getAnswers() != null) {
                    throw new IllegalArgumentException("Answers must be null for scale questions");
                }
                question.setScaleMin(questionDto.getScaleMin());
                question.setScaleMax(questionDto.getScaleMax());
                if (questionsMap.get(questionDto.getNextQuestionId()) == null) {
                    throw new IllegalArgumentException("Question id " + questionDto.getNextQuestionId() + " is not found");
                }
                question.setNextQuestion(questionsMap.get(questionDto.getNextQuestionId()));
            }

            if (questionDto.getQuestionType() == QuestionType.TEXT) {
                if (questionDto.getNextQuestionId() == null) {
                    throw new IllegalArgumentException("Next question id must be specified for question id " + questionDto.getQuestionId());
                }
                if (questionDto.getScaleMin() != null || questionDto.getScaleMax() != null) {
                    throw new IllegalArgumentException("Scale min and max must be null for text questions");
                }
                if (questionDto.getAnswers() != null) {
                    throw new IllegalArgumentException("Answers must be null for text questions");
                }
                if (questionsMap.get(questionDto.getNextQuestionId()) == null) {
                    throw new IllegalArgumentException("Question id " + questionDto.getNextQuestionId() + " is not found");
                }
                question.setNextQuestion(questionsMap.get(questionDto.getNextQuestionId()));
            }
        }

        return questionsMap;
    }

    /**
     * Checks if questions order is correct
     * @param visitStatus
     * @param currQuestion
     * @return
     */
    private boolean checkQuestionsOrder(Map<Long, Integer> visitStatus, Question currQuestion) {
        visitStatus.put(currQuestion.getQuestionId(), 1);

        if (currQuestion.getQuestionType() == QuestionType.SINGLE_CHOICE) {
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
        } else {
            Question nextQuestion = currQuestion.getNextQuestion();

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

    /**
     * Checks if user response is correct
     * @param userResponse
     * @return
     */
    private boolean checkUserResponse(UserResponse userResponse) {
        Poll poll = userResponse.getPoll();
        Question question = poll.getStartQuestion();
        int questionsCount = userResponse.getAnswers().size();
        while (questionsCount != 0) {
            final Question currQuestion = question;
            if (currQuestion.getQuestionType().equals(QuestionType.MULTIPLE_CHOICE)) {
                List<UserAnswer> userAnswers =
                userResponse.getAnswers().stream()
                        .filter(answer -> answer.getQuestion().getQuestionId().equals(currQuestion.getQuestionId())).toList();
                if (userAnswers.size() == 0) {
                    return false;
                }
                List<Long> answerOptionsIds = userAnswers.stream()
                        .map(answer -> answer.getAnswerOption().getId()).toList();
                if (answerOptionsIds.size() != answerOptionsIds.stream().distinct().count()) {
                    return false;
                }
                question = currQuestion.getNextQuestion();
                questionsCount -= userAnswers.size();
            }
            else {
                UserAnswer userAnswer = userResponse.getAnswers().stream()
                        .filter(answer -> answer.getQuestion().getQuestionId().equals(currQuestion.getQuestionId()))
                        .findFirst()
                        .orElse(null);
                if (userAnswer == null) {
                    return false;
                }
                if (currQuestion.getQuestionType().equals(QuestionType.SINGLE_CHOICE)) {
                    question = userAnswer.getAnswerOption().getNextQuestion();
                }
                else {
                    question = currQuestion.getNextQuestion();
                }
                questionsCount--;
            }
            if (questionsCount == 0) {
                return question == currQuestion;
            }
            if (question == currQuestion) {
                return false;
            }
        }

        return false;
    }

    /**
     * Converts poll dto to poll entity
     * @param pollDto
     * @return
     */
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

        if (pollDto.getTagIds() != null) {
            for (var tagId : pollDto.getTagIds()) {
                poll.getTags().add(tagService.getById(tagId));
            }
        }

        return poll;
    }

    /**
     * Converts xlsx file to poll entity
     * @param workbook
     * @return
     * @throws MethodArgumentNotValidException
     * @throws NoSuchMethodException
     */
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

    /**
     * Reads poll from xlsx file
     * @param workbook
     * @return
     */
    private PollDto readPoll(Workbook workbook) {
        PollDto pollDto = new PollDto();
        try {
            Sheet sheet = workbook.getSheetAt(0);

            pollDto.setName(sheet.getRow(0).getCell(1).getStringCellValue());
            if (sheet.getRow(0).getCell(4).getCellType().equals(CellType.NUMERIC)) {
                pollDto.setUserId(String.valueOf((long) sheet.getRow(0).getCell(4).getNumericCellValue()));
            } else {
                pollDto.setUserId(sheet.getRow(0).getCell(4).getStringCellValue());
            }
            pollDto.setDescription(sheet.getRow(1).getCell(1).getStringCellValue());
            pollDto.setStartQuestionId((long) sheet.getRow(3).getCell(1).getNumericCellValue());

            int currRow = 6;

            List<QuestionDto> questions = new ArrayList<>();

            while (sheet.getRow(currRow) != null && sheet.getRow(currRow).getCell(0) != null && sheet.getRow(currRow).getCell(0).getCellType() != CellType.BLANK) {
                Row row = sheet.getRow(currRow);

                QuestionDto questionDto = new QuestionDto();

                questionDto.setQuestionId((long) row.getCell(0).getNumericCellValue());
                String questionType = row.getCell(1).getStringCellValue();

                if (questionType.equals("Открытый")) {
                    questionDto.setQuestionType(QuestionType.TEXT);
                } else if (questionType.equals("Единственный выбор")) {
                    questionDto.setQuestionType(QuestionType.SINGLE_CHOICE);
                } else if (questionType.equals("Множественный выбор")) {
                    questionDto.setQuestionType(QuestionType.MULTIPLE_CHOICE);
                } else if (questionType.equals("Шкала")) {
                    questionDto.setQuestionType(QuestionType.SCALE);
                } else {
                    throw new IllegalArgumentException("Question type " + questionType + " is not found");
                }

                questionDto.setQuestionText(row.getCell(2).getStringCellValue());

                if (questionDto.getQuestionType().equals(QuestionType.SCALE)) {
                    questionDto.setScaleMin((long)row.getCell(4).getNumericCellValue());
                    questionDto.setScaleMax((long)row.getCell(5).getNumericCellValue());
                }

                if (!questionDto.getQuestionType().equals(QuestionType.SINGLE_CHOICE)) {
                    questionDto.setNextQuestionId((long) row.getCell(3).getNumericCellValue());
                }

                if (questionDto.getQuestionType().equals(QuestionType.SCALE) || questionDto.getQuestionType().equals(QuestionType.TEXT)) {
                    currRow++;
                    questions.add(questionDto);
                } else {
                    List<AnswerOptionDto> answers = new ArrayList<>();

                    AnswerOptionDto firstAnswer = new AnswerOptionDto();

                    firstAnswer.setAnswerText(sheet.getRow(currRow).getCell(6).getStringCellValue());
                    if (questionDto.getQuestionType().equals(QuestionType.SINGLE_CHOICE)) {
                        firstAnswer.setNextQuestionId((long) sheet.getRow(currRow).getCell(7).getNumericCellValue());
                    }

                    answers.add(firstAnswer);

                    int answerRow = currRow + 1;
                    while (sheet.getRow(answerRow) != null && (sheet.getRow(answerRow).getCell(0) == null || sheet.getRow(answerRow).getCell(0).getCellType() == CellType.BLANK)) {
                        AnswerOptionDto answerOptionDto = new AnswerOptionDto();

                        answerOptionDto.setAnswerText(sheet.getRow(answerRow).getCell(6).getStringCellValue());
                        if (questionDto.getQuestionType().equals(QuestionType.SINGLE_CHOICE)) {
                            answerOptionDto.setNextQuestionId((long) sheet.getRow(answerRow).getCell(7).getNumericCellValue());
                        }

                        answers.add(answerOptionDto);
                        answerRow++;
                    }

                    questionDto.setAnswers(answers);
                    questions.add(questionDto);
                    currRow = answerRow;
                }
            }

            pollDto.setQuestions(questions);
        } catch (Exception e) {
            throw new IllegalArgumentException("Excel file is not valid!");
        }

        return pollDto;
    }

    /**
     * Converts UserResponse dto to UserResponse entity
     * @param userResponseDto
     * @return
     */
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
            if (question.getQuestionType() == QuestionType.TEXT) {
                if (userAnswerDto.getAnswerText() == null || userAnswerDto.getAnswerText().isEmpty()) {
                    throw new IllegalArgumentException("Answer text is empty");
                }
                userAnswer.setAnswerText(userAnswerDto.getAnswerText());
            }
            if (question.getQuestionType() == QuestionType.SCALE) {
                if (userAnswerDto.getScaleValue() == null) {
                    throw new IllegalArgumentException("Scale value is empty");
                }
                if (userAnswerDto.getScaleValue() < question.getScaleMin() || userAnswerDto.getScaleValue() > question.getScaleMax()) {
                    throw new IllegalArgumentException("Scale value is not valid");
                }
                userAnswer.setScaleValue(userAnswerDto.getScaleValue());
            }
            if (question.getQuestionType() == QuestionType.SINGLE_CHOICE || question.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
                userAnswer.setAnswerOption(question.getAnswers().stream()
                        .filter(answerOption -> answerOption.getId().equals(userAnswerDto.getAnswerId()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Answer not found")));
            }

            userAnswers.add(userAnswer);
        }

        userResponse.setAnswers(userAnswers);

        if (!checkUserResponse(userResponse)) {
            throw new IllegalArgumentException("User response is not valid");
        }

        return userResponse;
    }
}
