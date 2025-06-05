package com.paymentsystemex.global.messageQueue;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeadLetterQueueService {

    public <T> void enqueue(List<T> objects) {
        //Dead Letter Queue에 enqueue
    }

    public <T> void enqueue(T objects) {
        //Dead Letter Queue에 enqueue
    }

}
