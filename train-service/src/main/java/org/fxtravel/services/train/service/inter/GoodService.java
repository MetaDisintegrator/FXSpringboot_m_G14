package org.fxtravel.services.train.service.inter;

public interface GoodService {
    boolean checkAndGet(int id, int count, Object data);

    void putBack(int id, int count, Object data);
}

