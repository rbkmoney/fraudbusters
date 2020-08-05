ALTER TABLE fraud.events_unique ADD COLUMN mobile UInt8;
ALTER TABLE fraud.events_unique ADD COLUMN recurrent UInt8;

ALTER TABLE fraud.payment ADD COLUMN mobile UInt8;
ALTER TABLE fraud.payment ADD COLUMN recurrent UInt8;

ALTER TABLE fraud.refund ADD COLUMN mobile UInt8;
ALTER TABLE fraud.refund ADD COLUMN recurrent UInt8;

ALTER TABLE fraud.chargeback ADD COLUMN mobile UInt8;
ALTER TABLE fraud.chargeback ADD COLUMN recurrent UInt8;