package io.app.arbittrading.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BybitSymbolsResponse {
    private String ret_msg;
    private List<BybitSymbol> result;
}
