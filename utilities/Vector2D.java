package utilities;

import java.io.Serializable;
import java.util.Objects;

/**
 * Mutable vectors objects used to represent different values (like position, velocity, direction) throughout the game.
 */
public final class Vector2D implements Serializable {
    public double x, y; //x and y positions

    // compare for equality
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2D vector2D = (Vector2D) o;
        return Double.compare(vector2D.x, x) == 0 &&
                Double.compare(vector2D.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    // constructor for zero vector
    public Vector2D() {
        this.x = 0;
        this.y = 0;
    }

    // constructor for vector with given coordinates
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // constructor that copies the argument vector
    public Vector2D(Vector2D v) {
        this.x = v.x;
        this.y = v.y;
    }

    // set coordinates
    public Vector2D set(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    // set coordinates based on argument vector
    public Vector2D set(Vector2D v) {
        this.x = v.x;
        this.y = v.y;
        return this;
    }

    // String for displaying vector as text
    @Override
    public String toString() {
        return "Vector2D{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    //  magnitude (= "length") of this vector
    public double mag() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    // angle between vector and horizontal axis in radians in range [-PI,PI]
    // can be calculated using Math.atan2
    public double angle() {
        return Math.atan2(this.y, this.x);
    }

    // angle between this vector and another vector in range [-PI,PI]
    public double angle(Vector2D other) {
        return Math.atan2(this.x * other.y - this.y * other.x, this.x * other.x + this.y * other.y);
    }

    // add argument vector
    public Vector2D add(Vector2D v) {
        this.x += v.x;
        this.y += v.y;
        return this;
    }

    // add values to coordinates
    public Vector2D add(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }

    // weighted add - surprisingly useful
    public Vector2D addScaled(Vector2D v, double fac) {
        this.x += v.x * fac;
        this.y += v.y * fac;
        return this;
    }

    // subtract argument vector
    public Vector2D subtract(Vector2D v) {
        this.x -= v.x;
        this.y -= v.y;
        return this;
    }

    // subtract values from coordinates
    public Vector2D subtract(double x, double y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    // multiply with factor
    public Vector2D mult(double fac) {
        this.x *= fac;
        this.y *= fac;
        return this;
    }

    // rotate by angle given in radians
    public Vector2D rotate(double angle) {
        return this.set(x * Math.cos(angle) - y * Math.sin(angle), x * Math.sin(angle) + y * Math.cos(angle));
    }

    // "dot product" ("scalar product") with argument vector
    public double dot(Vector2D v) {
        return (this.x * v.x + this.y * v.y);
    }

    // distance to argument vector
    public double dist(Vector2D v) {
        return Math.sqrt((this.x - v.x) * (this.x - v.x) + (this.y - v.y) * (this.y - v.y));
    }

    // normalise vector so that magnitude becomes 1
    public Vector2D normalise() {
        double normalise = 1.0 / Math.sqrt(this.x * this.x + this.y * this.y);
        this.x *= normalise;
        this.y *= normalise;
        return this;
    }

    // wrap-around operation, assumes w> 0 and h>0
    public Vector2D wrap(double w, double h) {
        x = (x + w) % w;
        y = (y + h) % h;
        return this;
    }

    // construct vector with given polar coordinates
    public static Vector2D polar(double angle, double mag) {
        return new Vector2D(mag * Math.cos(angle), mag * Math.sin(angle));
    }

}