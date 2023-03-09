package io.app.arbittrading.binance.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "binance")
public class Binance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
