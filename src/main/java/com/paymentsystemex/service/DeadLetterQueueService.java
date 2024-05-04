package com.paymentsystemex.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class DeadLetterQueueService {

    public <T> void enqueue(List<T> objects) {
        //Dead Letter Queue에 enqueue
    }

    public <T> void enqueue(T objects) {
        //Dead Letter Queue에 enqueue
    }

}
