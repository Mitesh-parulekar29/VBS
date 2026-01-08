package com.vbs.demo.controller;

import  com.vbs.demo.Dto.LoginDto;
import com.vbs.demo.Dto.DisplayDto;

import com.vbs.demo.Dto.UpdateDto;
import com.vbs.demo.models.History;
import com.vbs.demo.models.Transaction;
import com.vbs.demo.models.User;
import com.vbs.demo.repositories.HistoryRepo;
import com.vbs.demo.repositories.TransactionRepo;
import com.vbs.demo.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    UserRepo userRepo;
    @Autowired
    HistoryRepo historyRepo;
    @Autowired
    TransactionRepo transactionRepo;

    @PostMapping("/register")
    public String register(@RequestBody User user){

        userRepo.save(user);
        return "Signup Successful";
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginDto u){
        User user = userRepo.findByUsername(u.getUsername());
        if(user==null)
        {
            return "User Not Found";
        }
        if(!u.getPassword().equals(user.getPassword())){
            return "Password is incorrect";
        }
        if(!u.getRole().equals(user.getRole())){
            return "Incorrect Role";
        }
        return String.valueOf(user.getId());
    }

    @GetMapping("/get-details/{id}")
    public DisplayDto display(@PathVariable int id){
        User user = userRepo.findById(id).orElseThrow(()->new RuntimeException("User Not Found"));
        DisplayDto displayDto =new DisplayDto();

        displayDto.setName(user.getName());
        displayDto.setBalance(user.getBalance());
        displayDto.setUsername(user.getUsername());

        return displayDto;
    }
    @PostMapping("/update")
    public String update(@RequestBody UpdateDto up){

        User user = userRepo.findById(up.getId())
                .orElseThrow(() -> new RuntimeException("User Not Found"));


        if ("name".equalsIgnoreCase(up.getKey())) {
            if(!up.getValue().matches("^[A-Za-z ]+$")) return "Please give appropriate input";

            if(up.getValue().equals(user.getName())){return "Name cannot be same";}
            User name = userRepo.findByName(up.getValue());
            if(name!=null)return "This name already exists";

            user.setName(up.getValue());

        }
        else if ("password".equalsIgnoreCase(up.getKey())) {
            if(up.getValue().equals(user.getPassword())){return "Password cannot be same";}


            user.setPassword(up.getValue());


        }
        else if ("email".equalsIgnoreCase(up.getKey())) {
            if(!up.getValue().matches("^[a-z0-9+_.]+@[a-z+.]+\\.[a-z]{2,}$")) return "Please give appropriate input";
            if(up.getValue().equals(user.getEmail())){return "Email already exists";}

            User exist = userRepo.findByEmail(up.getValue());
            if(exist!=null) return "Email already exist";

            user.setEmail(up.getValue());
            }
        else {
            return "Invalid Key";
        }

        userRepo.save(user);
        return "Updated Successfully";
    }

    @PostMapping("/add/{adminId}")
    public String add(@RequestBody User user,@PathVariable int adminId)
    {
        History history=new History();
        User user1=userRepo.findById(adminId).orElseThrow(()->new RuntimeException("User not found"));
        history.setDescription("Admin "+adminId+" created user "+user.getUsername());
        userRepo.save(user);
        if(user.getBalance()>0){
            User user2 = userRepo.findByUsername(user.getUsername());
            Transaction t = new Transaction();
            t.setAmount(user.getBalance());
            t.setCurrBalance(user.getBalance());
            t.setDescription("Rs"+user.getBalance()+"has been credited Successfully");
            t.setUserId(user2.getId());
            transactionRepo.save(t);
        }
        historyRepo.save(history);

        return "Added Successfully!";
    }

    @DeleteMapping("/delete-user/{userId}/admin/{adminId}")
    public String delete(@PathVariable int userId, @PathVariable int adminId){
        User user = userRepo.findById(userId).orElseThrow(()->new RuntimeException("Not found"));
        if(user.getBalance()>0){

            return "Balance Should be zero";
        }
        History h1 = new History();
        h1.setDescription("Admin "+ adminId  +" Deleted User "+user.getUsername());
        historyRepo.save(h1);
        userRepo.delete(user);
        return "User Deleted Successfully";

    }

    @GetMapping("/users")
    public List<User>getAllUser(@RequestParam String sortBy, @RequestParam String order)
    {
        Sort sort;
        if(order.equalsIgnoreCase("desc"))
        {
            sort = Sort.by(sortBy).descending();
        }
        else{
            sort = Sort.by(sortBy).ascending();
        }
        return userRepo.findAllByRole("customer",sort);
    }

    @GetMapping("/users/{keyword}")
    public List<User>getUser(@PathVariable String keyword)
    {
        return userRepo.findAllByUsernameContainingIgnoreCaseAndRole(keyword,"customer");
    }


}



