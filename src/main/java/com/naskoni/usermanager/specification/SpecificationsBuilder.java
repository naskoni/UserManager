package com.naskoni.usermanager.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SpecificationsBuilder<T> {

  private final List<SearchCriteria> params;

  public SpecificationsBuilder() {
    params = new ArrayList<>();
  }

  public SpecificationsBuilder<T> with(String key, String operation, Object value) {
    params.add(new SearchCriteria(key, operation, value));
    return this;
  }

  public Specification<T> build() {
    if (CollectionUtils.isEmpty(params)) {
      return null;
    }

    List<Specification<T>> specs = new ArrayList<>();
    for (SearchCriteria param : params) {
      specs.add(new SearchSpecification<>(param));
    }

    Specification<T> result = specs.get(0);
    for (int i = 1; i < specs.size(); i++) {
      result = Objects.requireNonNull(Specification.where(result)).and(specs.get(i));
    }

    return result;
  }
}
