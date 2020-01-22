package com.gkiss01.meetdebwebapi.service;

import com.gkiss01.meetdebwebapi.entity.Date;
import com.gkiss01.meetdebwebapi.utils.UserWithId;

public interface VoteService {
    Date createVote(Long dateId, UserWithId userDetails);
    Date deleteVote(Long dateId, UserWithId userDetails);
}
