package com.ecampus.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.ecampus.model.Semesters;
import com.ecampus.model.StudentRegistrations;
import com.ecampus.model.StudentSemesterResult;
import com.ecampus.model.Students;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class StudentSemesterResultRepositoryImpl implements CustomStudentSemesterResultRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> findSpiCpiListBySpecification(
            Long semesterId,
            String cpiOperator, BigDecimal cpiFromValue, BigDecimal cpiToValue,
            String spiOperator, BigDecimal spiFromValue, BigDecimal spiToValue,
            String condition,
            String orderBy, String orderType) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<StudentSemesterResult> root = query.from(StudentSemesterResult.class);

        // Joins
        Join<StudentSemesterResult, StudentRegistrations> regJoin = root.join("studentRegistration");
        Join<StudentRegistrations, Students> studentJoin = regJoin.join("students");
        Join<StudentRegistrations, Semesters> semesterJoin = regJoin.join("semesters");

        List<Predicate> predicates = new ArrayList<>();

        // Base conditions
        predicates.add(cb.greaterThan(regJoin.get("srgrowstate"), (short) 0));
        predicates.add(cb.greaterThan(studentJoin.get("stdrowstate"), (short) 0));
        predicates.add(cb.or(
                cb.notEqual(studentJoin.get("stdfield2"), "Cancelation"),
                cb.isNull(studentJoin.get("stdfield2"))
        ));

        if (semesterId != null) {
            predicates.add(cb.equal(semesterJoin.get("strid"), semesterId));
        }

        predicates.add(cb.greaterThan(root.get("rowState"), (short) 0));
        predicates.add(cb.isNotNull(root.get("cpiNumeric")));
        predicates.add(cb.isNotNull(root.get("spiNumeric")));

        // CPI filtering
        Predicate cpiPredicate = null;
        Expression<BigDecimal> cpiExpr = root.get("cpiNumeric");
        if (cpiOperator != null && cpiFromValue != null) {
            switch (cpiOperator) {
                case "=": cpiPredicate = cb.equal(cpiExpr, cpiFromValue); break;
                case ">": cpiPredicate = cb.greaterThan(cpiExpr, cpiFromValue); break;
                case ">=": cpiPredicate = cb.greaterThanOrEqualTo(cpiExpr, cpiFromValue); break;
                case "<": cpiPredicate = cb.lessThan(cpiExpr, cpiFromValue); break;
                case "<=": cpiPredicate = cb.lessThanOrEqualTo(cpiExpr, cpiFromValue); break;
                case "BETWEEN":
                    if (cpiToValue != null) {
                        cpiPredicate = cb.between(cpiExpr, cpiFromValue, cpiToValue);
                    }
                    break;
            }
        }

        // SPI filtering
        Predicate spiPredicate = null;
        Expression<BigDecimal> spiExpr = root.get("spiNumeric");
        if (spiOperator != null && spiFromValue != null) {
            switch (spiOperator) {
                case "=": spiPredicate = cb.equal(spiExpr, spiFromValue); break;
                case ">": spiPredicate = cb.greaterThan(spiExpr, spiFromValue); break;
                case ">=": spiPredicate = cb.greaterThanOrEqualTo(spiExpr, spiFromValue); break;
                case "<": spiPredicate = cb.lessThan(spiExpr, spiFromValue); break;
                case "<=": spiPredicate = cb.lessThanOrEqualTo(spiExpr, spiFromValue); break;
                case "BETWEEN":
                    if (spiToValue != null) {
                        spiPredicate = cb.between(spiExpr, spiFromValue, spiToValue);
                    }
                    break;
            }
        }

        // Combine filters
        if (cpiPredicate != null && spiPredicate != null) {
            if ("or".equalsIgnoreCase(condition)) {
                predicates.add(cb.or(cpiPredicate, spiPredicate));
            } else {
                predicates.add(cb.and(cpiPredicate, spiPredicate));
            }
        } else if (cpiPredicate != null) {
            predicates.add(cpiPredicate);
        } else if (spiPredicate != null) {
            predicates.add(spiPredicate);
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // SELECT fields
        query.multiselect(
                studentJoin.get("stdinstid"),
                studentJoin.get("stdfirstname"),
                spiExpr,
                cpiExpr
        );

        // ORDER BY
        List<Order> orderList = new ArrayList<>();
        if (orderBy != null && !orderBy.isEmpty()) {
            Expression<?> orderExpr;
            switch (orderBy.toUpperCase()) {
                case "STDINSTID": orderExpr = studentJoin.get("stdinstid"); break;
                case "STDFIRSTNAME": orderExpr = studentJoin.get("stdfirstname"); break;
                case "SSRSPI": orderExpr = spiExpr; break;
                case "SSRCPI": orderExpr = cpiExpr; break;
                default: orderExpr = studentJoin.get("stdinstid"); break;
            }

            if ("DESC".equalsIgnoreCase(orderType)) {
                orderList.add(cb.desc(orderExpr));
            } else {
                orderList.add(cb.asc(orderExpr));
            }
        } else {
            orderList.add(cb.asc(studentJoin.get("stdinstid")));
        }

        query.orderBy(orderList);

        return entityManager.createQuery(query).getResultList();
    }
}
