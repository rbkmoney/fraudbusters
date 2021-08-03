INSERT INTO fraud.chargeback
(timestamp, eventTime, eventTimeHour, partyId, shopId, email, fingerprint, amount, currency, status, id,
 ip, bin, maskedPan, paymentTool, cardToken, paymentSystem, terminal, providerId, bankCountry, paymentId)
VALUES ('2020-05-06', 1588761208, 1588759200000, 'group_1', '2035728', 'email', '4bef59146f8e4640ab34915f84ddac8b',
        10500, 'RUB', 'rejected', '1DkraVdGJbs.1', '204.26.61.110', '666', '3125', 'bank_card',
        '477bba133c182267fe5f086924abdc5db71f77bfc27f01f2843f2cdc69d89f05', 'VISA', '123', '1', 'RUS', '1111vbd'),
       ('2020-05-06', 1588761208, 1588759200000, 'group_1', '2035728', 'email', '4bef59146f8e4640ab34915f84ddac8b',
        10500, 'RUB', 'rejected', '1DkraVdGJfs.1', '204.26.61.110', '666', '3125', 'bank_card',
        '477bba133c182267fe5f086924abdc5db71f77bfc27f01f2843f2cdc69d89f05', 'VISA', '123', '1', 'RUS', '1111vba'),
       ('2020-05-06', 1588761208, 1588759200000, 'group_1', '2035728', 'email_2', '5bef59146f8e4640ab34915f84ddac8b',
        50000, 'RUB', 'rejected', '1VMI0gIoAy0.2', '204.26.61.110', '666', '3125', 'bank_card',
        '477bba133c182267fe5f086924abdc5db71f77bfc27f01f2843f2cdc69d89f05', 'VISA', '123', '1', 'RUS', '1111vbc'),
       ('2020-05-06', 1588761208, 1588759200000, 'group_1', '2035728', 'email_2', '5bef59146f8e4640ab34915f84ddac8b',
        50000, 'RUB', 'cancelled', '1VMI3GwdR5s.1', '204.26.61.110', '666', '3125', 'bank_card',
        '477bba133c182267fe5f086924abdc5db71f77bfc27f01f2843f2cdc69d89f05', 'VISA', '123', '1', 'RUS', '1111vbf'),
       ('2020-05-06', 1588761208, 1588759200000, 'partyId_2', '2035728', 'email_2', '4bef59146f8e4640ab34915f84ddac8b',
        50000, 'RUB', 'accepted', '1DkraVdGJfr.1', '204.26.61.110', '666', '3125', 'bank_card',
        '477bba133c182267fe5f086924abdc5db71f77bfc27f01f2843f2cdc69d89f05', 'VISA', '123', '1', 'RUS', '1111vbe'),
       ('2020-05-06', 1588761208, 1588759200000, 'partyId_2', '2035729', 'email_2', '4bef59146f8e4640ab34915f84ddac8b',
        50000, 'RUB', 'accepted', '1DkraVdGJfs.1', '204.26.61.110', '666', '3125', 'bank_card',
        '477bba133c182267fe5f086924abdc5db71f77bfc27f01f2843f2cdc69d89f05', 'VISA', '123', '1', 'RUS', '1111vbp'),
       ('2019-12-05', 1587761208, 1587759200000, 'group_1', '2035728', 'email', '4bef59146f8e4640ab34915f84ddac8b',
        5000, 'RUB', 'cancelled', '1DkratTHbpg.1', '204.26.61.110', '666', '3125', 'bank_card',
        '477bba133c182267fe5f086924abdc5db71f77bfc27f01f2843f2cdc69d89f05', 'VISA', '123', '1', 'RUS', '1111vbdss'),
       ('2019-12-05', 1587761208, 1587759200000, 'group_1', '2035728', 'email', '4bef59146f8e4640ab34915f84ddac8b',
        5000, 'RUB', 'cancelled', '1DkratTHbpg.1', '204.26.61.110', '666', '3125', 'bank_card',
        '477bba133c182267fe5f086924abdc5db71f77bfc27f01f2843f2cdc69d89f05', 'VISA', '123', '1', 'RUS', '1111vbz'),
       ('2019-12-05', 1587761208, 1587759200000, 'group_1', '2035728', 'email', '4bef59146f8e4640ab34915f84ddac8b',
        5000, 'RUB', 'accepted', '1DkratTHbpg.1', '204.26.61.110', '666', '3125', 'bank_card',
        '477bba133c182267fe5f086924abdc5db71f77bfc27f01f2843f2cdc69d89f05', 'VISA', '123', '1', 'RUS',
        '1111vbuc'),
       ('2019-12-05', 1587761208, 1587759200000, 'group_1', '2035728', 'email', '4bef59146f8e4640ab34915f84ddac8b',
        5000, 'RUB', 'rejected', '1DkratTHbpg.1', '204.26.61.110', '666', '3125', 'bank_card',
        '477bba133c182267fe5f086924abdc5db71f77bfc27f01f2843f2cdc69d89f05', 'VISA', '123', '1', 'RUS', '1111vbtt')
