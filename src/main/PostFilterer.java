package main;

import java.util.List;

/**
 * Created by mats on 26.07.2015.
 */
public interface PostFilterer {

    /**
     * @param transactions
     * @return the filtered list of transactions that are valid for this main.Post
     */
    List<Transaction> filter(List<Transaction> transactions);

}
