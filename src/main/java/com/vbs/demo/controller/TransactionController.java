package com.vbs.demo.controller;


import com.vbs.demo.Dto.TransactionDto;
import com.vbs.demo.Dto.TransferDto;
import com.vbs.demo.models.Transaction;
import com.vbs.demo.models.User;
import com.vbs.demo.repositories.TransactionRepo;
import com.vbs.demo.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class TransactionController
{
    @Autowired
    UserRepo userRepo;

    @Autowired
    TransactionRepo transactionRepo;

    @PostMapping("/deposit")
    public String deposit(@RequestBody TransactionDto obj)
    {
        User user = userRepo.findById(obj.getId()).orElseThrow(()->new RuntimeException("Not Found..!!"));
        double newBalance = user.getBalance()+obj.getAmount();
        user.setBalance(newBalance);
        userRepo.save(user);

        Transaction t = new Transaction();
        t.setAmount(obj.getAmount());
        t.setDescription("Rs. "+obj.getAmount()+" Deposited Successfully");
        t.setCurrBalance(newBalance);
        t.setUserId(obj.getId());

        transactionRepo.save(t);
        return "Deposit Successfully..!!";

    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestBody TransactionDto obj)
    {
        User user = userRepo.findById(obj.getId()).orElseThrow(()->new RuntimeException("Not Found..!!"));
        double newBalance = user.getBalance()-obj.getAmount();

        if(newBalance<=0)
        {
            return"Balance not sufficient..!!";
        }

        user.setBalance(newBalance);
        userRepo.save(user);

        Transaction t = new Transaction();
        t.setAmount(obj.getAmount());
        t.setDescription("Rs. "+obj.getAmount()+" Withdrawal Successful");
        t.setCurrBalance(newBalance);
        t.setUserId(obj.getId());

        transactionRepo.save(t);
        return "Withdrawal Successfully..!!";

    }

    @PostMapping("/transfer")
    public String transfer(@RequestBody TransferDto obj) {
        User sender = userRepo.findById(obj.getId()).orElseThrow(() -> new RuntimeException("Not Found..!!"));
        User rec = userRepo.findByusername(obj.getUsername());

        if (rec == null) {
            return "Username not found..!!";
        }
        if (obj.getAmount() < 1) {
            return "Invalid Amount";
        }
        if (sender.getId() == rec.getId()) {
            return "Self transfer not allowed..!!";
        }

        double sbalance = sender.getBalance() - obj.getAmount();
        double rbalance = rec.getBalance() + obj.getAmount();

        if (sbalance < 0) {
            return "Insufficient Balance..!!";
        }

        sender.setBalance(sbalance);
        rec.setBalance(rbalance);

        userRepo.save(sender);
        userRepo.save(rec);

        Transaction t1 = new Transaction();
        Transaction t2 = new Transaction();

        t1.setAmount(obj.getAmount());
        t1.setCurrBalance(sbalance);
        t1.setDescription("Rs." + obj.getAmount() + "Sent to user" + obj.getUsername());
        t1.setUserId(obj.getId());

        t2.setAmount(obj.getAmount());
        t2.setCurrBalance(rbalance);
        t2.setDescription("Rs." + obj.getAmount() + "Received from user" + sender.getUsername());
        t2.setUserId(rec.getId());

        transactionRepo.save(t1);
        transactionRepo.save(t2);
        return "Transfer Successful..!!";
    }

    @GetMapping("/passbook/{id}")
    public List<Transaction> getPassbook(@PathVariable int id)
    {
        return transactionRepo.findByUserId(id);
    }

}
