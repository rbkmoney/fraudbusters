ALTER TABLE fraud.payment ADD COLUMN checkedTemplate String;
ALTER TABLE fraud.payment ADD COLUMN resultStatus String;

ALTER TABLE fraud.payment ADD COLUMN checkedResultsJson String;