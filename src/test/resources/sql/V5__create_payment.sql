ALTER TABLE fraud.events_unique ADD COLUMN payerType String;
ALTER TABLE fraud.events_unique ADD COLUMN tokenProvider String;

ALTER TABLE fraud.payment ADD COLUMN payerType String;
ALTER TABLE fraud.payment ADD COLUMN tokenProvider String;

ALTER TABLE fraud.refund ADD COLUMN payerType String;
ALTER TABLE fraud.refund ADD COLUMN tokenProvider String;

ALTER TABLE fraud.chargeback ADD COLUMN payerType String;
ALTER TABLE fraud.chargeback ADD COLUMN tokenProvider String;