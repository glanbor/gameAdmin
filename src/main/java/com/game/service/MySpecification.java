package com.game.repository;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;

@Component
public class MySpecification<T> {

    public Specification<T> filterName(String name) {
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (name == null) return null;
                return criteriaBuilder.like(root.get("name"), "%" + name + "%");
            }
        };
    }

    //  The method structure is as previous one, but uses lambda and ternary operator
    public Specification<T> filterTitle(String title) {
        return (root, query, criteriaBuilder) -> title == null ? null :
                criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }

    public Specification<T> filterBirthday(Long after, Long before) {
        return (root, query, criteriaBuilder) -> {
            if (after == null && before == null) return null;
            if (after == null) return criteriaBuilder.lessThanOrEqualTo(root.get("birthday"), new Date(before));
            if (before == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("birthday"), new Date(after));
            return criteriaBuilder.between(root.get("birthday"), new Date(after), new Date(before));
        };
    }

    public Specification<T> filterExperience(Integer min, Integer max) {
        return (root, query, criteriaBuilder) -> {
            if (min == null && max == null) return null;
            if (min == null) return criteriaBuilder.lessThanOrEqualTo(root.get("experience"), max);
            if (max == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("experience"), min);
            return criteriaBuilder.between(root.get("experience"), min, max);
        };
    }

    public Specification<T> filterLevel(Integer min, Integer max) {
        return (root, query, criteriaBuilder) -> {
            if (min == null && max == null) return null;
            if (min == null) return criteriaBuilder.lessThanOrEqualTo(root.get("level"), max);
            if (max == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("level"), min);
            return criteriaBuilder.between(root.get("level"), min, max);
        };
    }

    public Specification<T> filterRace(Race race) {
        return (root, query, criteriaBuilder) -> race == null ? null :
                criteriaBuilder.equal(root.get("race"), race);
    }

    public Specification<T> filterProfession(Profession profession) {
        return (root, query, criteriaBuilder) -> profession == null ? null :
                criteriaBuilder.equal(root.get("profession"), profession);
    }

    public Specification<T> filterBanned(Boolean isBanned) {
        return (root, query, criteriaBuilder) -> {
            if (isBanned == null) return null;
            if (isBanned) return criteriaBuilder.isTrue(root.get("banned"));
            return criteriaBuilder.isFalse(root.get("banned"));
        };
    }

    public Specification<T> filterAll(String name, String title, Race race, Profession profession,
                                      Long after, Long before, Boolean banned,
                                      Integer minExperience, Integer maxExperience,
                                      Integer minLevel, Integer maxLevel) {
        return Specification.where(
                filterName(name)
                .and(filterTitle(title))
                .and(filterRace(race))
                .and(filterProfession(profession))
                .and(filterBirthday(after, before))
                .and(filterBanned(banned))
                .and(filterExperience(minExperience, maxExperience))
                .and(filterLevel(minLevel, maxLevel)));
    }
}
