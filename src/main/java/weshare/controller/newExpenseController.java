package weshare.controller;

import io.javalin.core.validation.Validator;
import io.javalin.http.Handler;
import weshare.model.Expense;
import weshare.model.Person;
import weshare.persistence.ExpenseDAO;
import weshare.server.Routes;
import weshare.server.ServiceRegistry;
import weshare.server.WeShareServer;

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.util.Objects;

import static weshare.model.MoneyHelper.amountOf;

public class newExpenseController {

    public static final Handler view = context -> {
        context.render("newexpense.html");
    };
    public static final Handler saveExpense = context -> {
    Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);
    ExpenseDAO expenseDAO = ServiceRegistry.lookup(ExpenseDAO.class);
    String description = context.formParam("description");
    String date = context.formParam("date");
    String amnt = context.formParam("amount");
    MonetaryAmount amount = amountOf(Long.parseLong(Objects.requireNonNull(amnt)));
    String [] dateList = Objects.requireNonNull(date).split("-");
    LocalDate correctDate = LocalDate.of(Integer.parseInt(dateList[0]),Integer.parseInt(dateList[1]),Integer.parseInt(dateList[2]));
    Expense expense = expenseDAO.save(new Expense(personLoggedIn ,description,amount,correctDate));
    System.out.println(context.path());
    context.redirect(Routes.EXPENSES);
    };


}
