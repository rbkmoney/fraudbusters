package com.rbkmoney.fraudbusters.domain.dgraph;

import com.rbkmoney.fraudbusters.domain.dgraph.side.DgraphEmail;
import com.rbkmoney.fraudbusters.domain.dgraph.side.DgraphFingerprint;
import com.rbkmoney.fraudbusters.domain.dgraph.side.DgraphToken;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
public abstract class DgraphExtendedSideObject extends DgraphSideObject {

    public DgraphExtendedSideObject(String lastActTime) {
        super(lastActTime);
    }

    private List<DgraphToken> tokens;
    private List<DgraphEmail> emails;
    private List<DgraphFingerprint> fingerprints;

}
