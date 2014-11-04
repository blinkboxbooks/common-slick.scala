# Change log

## 0.3.0 ([#6](https://git.mobcastdev.com/Platform/common-slick/pull/6) 2014-11-03 18:26:11)

Simplified exceptions to make them easier to use in tests

### Breaking changes

Database exceptions no longer take type parameters. Instead, the `cause` has the type used as a lower bound before.

## 0.2.2 ([#5](https://git.mobcastdev.com/Platform/common-slick/pull/5) 2014-11-03 18:11:30)

MySQL exception transformer handles more cases

### Improvement

Now catches integrity constraint violations caused by MySQL batch inserts.

## 0.2.1 ([#4](https://git.mobcastdev.com/Platform/common-slick/pull/4) 2014-11-03 15:34:13)

H2 exception transformer handles more constraint violation cases

### Improvement

Constraint violations can happen on updates as well. Instead of adding another case for `org.h2.jdbc.JdbcBatchUpdateException`, I made this more general. We relied on `java.sql.SQLException.getErroCode()` anyway.

## 0.2.0 ([#3](https://git.mobcastdev.com/Platform/common-slick/pull/3) 2014-10-07 11:06:59)

Added a `DatabaseHealthCheck` class

### New features

- Now provides a `DatabaseHealthCheck` class to allow you to make
service health checks more robust.

## 0.1.1 ([#2](https://git.mobcastdev.com/Platform/common-slick/pull/2) 2014-09-04 15:31:35)

Introduce cross-compilation for 2.11.2

### Improvements

* Cross compilation for 2.11.2

## 0.1.0 ([#1](https://git.mobcastdev.com/Platform/common-slick/pull/1) 2014-08-27 09:05:57)

Initial implementation of cake-pattern-like slick support for multiple DBs

New features

This PR abstracts the database work done in the auth-service into a separate library.

