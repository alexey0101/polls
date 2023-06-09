package com.graduate.polls.service.api;

import com.graduate.polls.models.PollStatistic;

public interface StatisticService {
    public PollStatistic getPollStatistics(Long pollId) throws Exception;
    byte[] createPollReport(Long pollId) throws Exception;
}
