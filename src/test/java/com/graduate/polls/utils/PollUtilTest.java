package com.graduate.polls.utils;

import com.graduate.polls.models.Poll;
import com.graduate.polls.models.Question;
import com.graduate.polls.models.QuestionType;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class PollUtilTest {

    /**
     * Test converting excel file to Poll object with valid data
     * @throws IOException
     * @throws MethodArgumentNotValidException
     * @throws NoSuchMethodException
     */
    @Test
    void convertExcelToPollSuccess() throws IOException, MethodArgumentNotValidException, NoSuchMethodException {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(null, null) {
        });
        testConvertExcelToPoll("sample_poll.xlsx");
    }

    /**
     * Test converting excel file to Poll object with invalid data
     * @throws IOException
     * @throws MethodArgumentNotValidException
     * @throws NoSuchMethodException
     */
    @Test
    void convertExcelToPollFail() throws IOException, MethodArgumentNotValidException, NoSuchMethodException {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(null, null) {
        });
        try {
            testConvertExcelToPoll("sample_poll_invalid.xlsx");
            assert false;
        } catch (Exception e) {
            assert true;
        }
    }

    private void testConvertExcelToPoll(String fileName) throws IOException, MethodArgumentNotValidException, NoSuchMethodException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        Workbook workbook = WorkbookFactory.create(inputStream);

        PollUtil pollUtil = new PollUtil(null, null);
        Poll poll = pollUtil.convertToEntity(workbook);

        assertEquals("Тестовый", poll.getName());
        assertEquals("Тестовый опрос", poll.getDescription());
        assertEquals(1, poll.getStartQuestion().getQuestionId());
        assertEquals("123", poll.getUserId());
        assertEquals(4, poll.getQuestions().size());

        Question firstQuestion = poll.getQuestions().get(0);
        assertEquals(1, firstQuestion.getQuestionId());
        assertEquals(QuestionType.TEXT, firstQuestion.getQuestionType());
        assertEquals("Тест", firstQuestion.getQuestionText());
        assertEquals(2, firstQuestion.getNextQuestion().getQuestionId());

        Question secondQuestion = poll.getQuestions().get(1);
        assertEquals(2, secondQuestion.getQuestionId());
        assertEquals(QuestionType.MULTIPLE_CHOICE, secondQuestion.getQuestionType());
        assertEquals("Тест", secondQuestion.getQuestionText());
        assertEquals(3, secondQuestion.getNextQuestion().getQuestionId());
        assertEquals(4, secondQuestion.getAnswers().size());

        for (int i = 0; i < secondQuestion.getAnswers().size(); i++) {
            assertEquals("тест" + (i + 1), secondQuestion.getAnswers().get(i).getAnswerText());
        }

        Question thirdQuestion = poll.getQuestions().get(2);
        assertEquals(3, thirdQuestion.getQuestionId());
        assertEquals(QuestionType.SINGLE_CHOICE, thirdQuestion.getQuestionType());
        assertEquals("Тест", thirdQuestion.getQuestionText());
        assertEquals(4, thirdQuestion.getAnswers().size());

        for (int i = 0; i < thirdQuestion.getAnswers().size(); i++) {
            assertEquals("тест" + (i + 1), thirdQuestion.getAnswers().get(i).getAnswerText());
            if (i == 2) {
                assertEquals(3, thirdQuestion.getAnswers().get(i).getNextQuestion().getQuestionId());
            } else {
                assertEquals(4, thirdQuestion.getAnswers().get(i).getNextQuestion().getQuestionId());
            }
        }

        Question fourthQuestion = poll.getQuestions().get(3);
        assertEquals(4, fourthQuestion.getQuestionId());
        assertEquals(QuestionType.SCALE, fourthQuestion.getQuestionType());
        assertEquals("Тест", fourthQuestion.getQuestionText());
        assertEquals(4, fourthQuestion.getNextQuestion().getQuestionId());
        assertEquals(0, fourthQuestion.getScaleMin());
        assertEquals(10, fourthQuestion.getScaleMax());
    }



}