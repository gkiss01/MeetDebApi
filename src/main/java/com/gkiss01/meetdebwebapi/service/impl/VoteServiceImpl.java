package com.gkiss01.meetdebwebapi.service.impl;

import com.gkiss01.meetdebwebapi.entity.Date;
import com.gkiss01.meetdebwebapi.entity.Vote;
import com.gkiss01.meetdebwebapi.entity.idclass.ParticipantId;
import com.gkiss01.meetdebwebapi.entity.idclass.VoteId;
import com.gkiss01.meetdebwebapi.repository.DateRepository;
import com.gkiss01.meetdebwebapi.repository.ParticipantRepository;
import com.gkiss01.meetdebwebapi.repository.VoteRepository;
import com.gkiss01.meetdebwebapi.service.VoteService;
import com.gkiss01.meetdebwebapi.utils.UserWithId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VoteServiceImpl implements VoteService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private DateRepository dateRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Override
    @Transactional
    public List<Date> createVote(Long dateId, UserWithId userDetails) {
        Date date = dateRepository.findDateByIdCustom(dateId, userDetails.getUserId());

        if (date == null)
            throw new RuntimeException("Date not found!");

        if (date.getAccepted())
            throw new RuntimeException("Vote is already created!");

        Vote vote = new Vote(new VoteId(dateId, userDetails.getUserId()));
        participantRepository.deleteById_EventIdAndId_UserId(date.getEventId(), userDetails.getUserId());
        voteRepository.deleteByEventIdAndUserId(date.getEventId(), userDetails.getUserId());
        voteRepository.save(vote);
        return dateRepository.findDateByEventIdOrderByDateCustom(date.getEventId(), userDetails.getUserId());
    }

    @Override
    @Transactional
    public Date deleteVote(Long dateId, UserWithId userDetails) {
        Vote vote = voteRepository.findVoteById_DateIdAndId_UserId(dateId, userDetails.getUserId());

        if (vote == null)
            throw new RuntimeException("Vote not found!");

        voteRepository.delete(vote);
        return dateRepository.findDateByIdCustom(dateId, userDetails.getUserId());
    }
}