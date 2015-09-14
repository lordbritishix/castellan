package com.jjdevbros.castellan.common.serializer;

import com.google.common.collect.ImmutableMap;
import com.jjdevbros.castellan.common.model.EventModel;
import com.jjdevbros.castellan.common.model.WindowsLogEventId;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.type.TypeReference;
import org.elasticsearch.search.SearchHit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by lordbritishix on 13/09/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class SearchHitToEventModelSerializerTest {
    private SearchHitToEventModelSerializer fixture;
    private static final String EVENT_4800 = "{\"EventTime\":\"2015-09-12 21:19:12\",\"Hostname\":\"IE11Win7\",\"Keywords\":-9214364837600034816,\"EventType\":\"AUDIT_SUCCESS\",\"SeverityValue\":2,\"Severity\":\"INFO\",\"EventID\":4800,\"SourceName\":\"Microsoft-Windows-Security-Auditing\",\"ProviderGuid\":\"{54849625-5478-4994-A5BA-3E3B0328C30D}\",\"Version\":0,\"Task\":12551,\"OpcodeValue\":0,\"RecordNumber\":5329,\"ProcessID\":436,\"ThreadID\":2340,\"Channel\":\"Security\",\"Message\":\"The workstation was locked.\\r\\n\\r\\nSubject:\\r\\n\\tSecurity ID:\\t\\tS-1-5-21-3463664321-2923530833-3546627382-1000\\r\\n\\tAccount Name:\\t\\tIEUser\\r\\n\\tAccount Domain:\\t\\tIE11WIN7\\r\\n\\tLogon ID:\\t\\t0xa87ce\\r\\n\\tSession ID:\\t2\",\"Category\":\"Other Logon/Logoff Events\",\"Opcode\":\"Info\",\"TargetUserSid\":\"S-1-5-21-3463664321-2923530833-3546627382-1000\",\"TargetUserName\":\"IEUser\",\"TargetDomainName\":\"IE11WIN7\",\"TargetLogonId\":\"0xa87ce\",\"SessionId\":\"2\",\"EventReceivedTime\":\"2015-09-12 21:19:13\",\"SourceModuleName\":\"eventlog\",\"SourceModuleType\":\"im_msvistalog\"}";
    private static final String EVENT_4801 = "{\"EventTime\":\"2015-09-12 21:14:58\",\"Hostname\":\"IE11Win7\",\"Keywords\":-9214364837600034816,\"EventType\":\"AUDIT_SUCCESS\",\"SeverityValue\":2,\"Severity\":\"INFO\",\"EventID\":4801,\"SourceName\":\"Microsoft-Windows-Security-Auditing\",\"ProviderGuid\":\"{54849625-5478-4994-A5BA-3E3B0328C30D}\",\"Version\":0,\"Task\":12551,\"OpcodeValue\":0,\"RecordNumber\":5312,\"ProcessID\":436,\"ThreadID\":2340,\"Channel\":\"Security\",\"Message\":\"The workstation was unlocked.\\r\\n\\r\\nSubject:\\r\\n\\tSecurity ID:\\t\\tS-1-5-21-3463664321-2923530833-3546627382-1000\\r\\n\\tAccount Name:\\t\\tIEUser\\r\\n\\tAccount Domain:\\t\\tIE11WIN7\\r\\n\\tLogon ID:\\t\\t0xa87ce\\r\\n\\tSession ID:\\t2\",\"Category\":\"Other Logon/Logoff Events\",\"Opcode\":\"Info\",\"TargetUserSid\":\"S-1-5-21-3463664321-2923530833-3546627382-1000\",\"TargetUserName\":\"IEUser\",\"TargetDomainName\":\"IE11WIN7\",\"TargetLogonId\":\"0xa87ce\",\"SessionId\":\"2\",\"EventReceivedTime\":\"2015-09-12 21:14:59\",\"SourceModuleName\":\"eventlog\",\"SourceModuleType\":\"im_msvistalog\"}";
    private static final String EVENT_4802 = "{\"EventTime\":\"2015-09-12 21:26:07\",\"Hostname\":\"IE11Win7\",\"Keywords\":-9214364837600034816,\"EventType\":\"AUDIT_SUCCESS\",\"SeverityValue\":2,\"Severity\":\"INFO\",\"EventID\":4802,\"SourceName\":\"Microsoft-Windows-Security-Auditing\",\"ProviderGuid\":\"{54849625-5478-4994-A5BA-3E3B0328C30D}\",\"Version\":0,\"Task\":12551,\"OpcodeValue\":0,\"RecordNumber\":5351,\"ProcessID\":436,\"ThreadID\":2340,\"Channel\":\"Security\",\"Message\":\"The screen saver was invoked.\\r\\n\\r\\nSubject:\\r\\n\\tSecurity ID:\\t\\tS-1-5-21-3463664321-2923530833-3546627382-1000\\r\\n\\tAccount Name:\\t\\tIEUser\\r\\n\\tAccount Domain:\\t\\tIE11WIN7\\r\\n\\tLogon ID:\\t\\t0xa87ce\\r\\n\\tSession ID:\\t2\",\"Category\":\"Other Logon/Logoff Events\",\"Opcode\":\"Info\",\"TargetUserSid\":\"S-1-5-21-3463664321-2923530833-3546627382-1000\",\"TargetUserName\":\"IEUser\",\"TargetDomainName\":\"IE11WIN7\",\"TargetLogonId\":\"0xa87ce\",\"SessionId\":\"2\",\"EventReceivedTime\":\"2015-09-12 21:26:08\",\"SourceModuleName\":\"eventlog\",\"SourceModuleType\":\"im_msvistalog\"}";
    private static final String EVENT_4803 = "{\"EventTime\":\"2015-09-12 21:34:08\",\"Hostname\":\"IE11Win7\",\"Keywords\":-9214364837600034816,\"EventType\":\"AUDIT_SUCCESS\",\"SeverityValue\":2,\"Severity\":\"INFO\",\"EventID\":4803,\"SourceName\":\"Microsoft-Windows-Security-Auditing\",\"ProviderGuid\":\"{54849625-5478-4994-A5BA-3E3B0328C30D}\",\"Version\":0,\"Task\":12551,\"OpcodeValue\":0,\"RecordNumber\":5352,\"ProcessID\":436,\"ThreadID\":2340,\"Channel\":\"Security\",\"Message\":\"The screen saver was dismissed.\\r\\n\\r\\nSubject:\\r\\n\\tSecurity ID:\\t\\tS-1-5-21-3463664321-2923530833-3546627382-1000\\r\\n\\tAccount Name:\\t\\tIEUser\\r\\n\\tAccount Domain:\\t\\tIE11WIN7\\r\\n\\tLogon ID:\\t\\t0xa87ce\\r\\n\\tSession ID:\\t2\",\"Category\":\"Other Logon/Logoff Events\",\"Opcode\":\"Info\",\"TargetUserSid\":\"S-1-5-21-3463664321-2923530833-3546627382-1000\",\"TargetUserName\":\"IEUser\",\"TargetDomainName\":\"IE11WIN7\",\"TargetLogonId\":\"0xa87ce\",\"SessionId\":\"2\",\"EventReceivedTime\":\"2015-09-12 21:34:09\",\"SourceModuleName\":\"eventlog\",\"SourceModuleType\":\"im_msvistalog\"}";
    private static final String EVENT_4647 = "{\"EventTime\":\"2015-09-13 18:08:59\",\"Hostname\":\"IE11Win7\",\"Keywords\":-9214364837600034816,\"EventType\":\"AUDIT_SUCCESS\",\"SeverityValue\":2,\"Severity\":\"INFO\",\"EventID\":4647,\"SourceName\":\"Microsoft-Windows-Security-Auditing\",\"ProviderGuid\":\"{54849625-5478-4994-A5BA-3E3B0328C30D}\",\"Version\":0,\"Task\":12545,\"OpcodeValue\":0,\"RecordNumber\":5391,\"ProcessID\":432,\"ThreadID\":2040,\"Channel\":\"Security\",\"Message\":\"User initiated logoff:\\r\\n\\r\\nSubject:\\r\\n\\tSecurity ID:\\t\\tS-1-5-21-3463664321-2923530833-3546627382-1000\\r\\n\\tAccount Name:\\t\\tIEUser\\r\\n\\tAccount Domain:\\t\\tIE11WIN7\\r\\n\\tLogon ID:\\t\\t0x17f8d\\r\\n\\r\\nThis event is generated when a logoff is initiated. No further user-initiated activity can occur. This event can be interpreted as a logoff event.\",\"Category\":\"Logoff\",\"Opcode\":\"Info\",\"TargetUserSid\":\"S-1-5-21-3463664321-2923530833-3546627382-1000\",\"TargetUserName\":\"IEUser\",\"TargetDomainName\":\"IE11WIN7\",\"TargetLogonId\":\"0x17f8d\",\"EventReceivedTime\":\"2015-09-13 18:09:01\",\"SourceModuleName\":\"eventlog\",\"SourceModuleType\":\"im_msvistalog\"}";
    private static final String EVENT_4648 = "{\"EventTime\":\"2015-09-13 18:02:33\",\"Hostname\":\"IE11Win7\",\"Keywords\":-9214364837600034816,\"EventType\":\"AUDIT_SUCCESS\",\"SeverityValue\":2,\"Severity\":\"INFO\",\"EventID\":4648,\"SourceName\":\"Microsoft-Windows-Security-Auditing\",\"ProviderGuid\":\"{54849625-5478-4994-A5BA-3E3B0328C30D}\",\"Version\":0,\"Task\":12544,\"OpcodeValue\":0,\"RecordNumber\":5386,\"ProcessID\":432,\"ThreadID\":496,\"Channel\":\"Security\",\"Message\":\"A logon was attempted using explicit credentials.\\r\\n\\r\\nSubject:\\r\\n\\tSecurity ID:\\t\\tS-1-5-18\\r\\n\\tAccount Name:\\t\\tIE11WIN7$\\r\\n\\tAccount Domain:\\t\\tWORKGROUP\\r\\n\\tLogon ID:\\t\\t0x3e7\\r\\n\\tLogon GUID:\\t\\t{00000000-0000-0000-0000-000000000000}\\r\\n\\r\\nAccount Whose Credentials Were Used:\\r\\n\\tAccount Name:\\t\\tIEUser\\r\\n\\tAccount Domain:\\t\\tIE11WIN7\\r\\n\\tLogon GUID:\\t\\t{00000000-0000-0000-0000-000000000000}\\r\\n\\r\\nTarget Server:\\r\\n\\tTarget Server Name:\\tlocalhost\\r\\n\\tAdditional Information:\\tlocalhost\\r\\n\\r\\nProcess Information:\\r\\n\\tProcess ID:\\t\\t0x16c\\r\\n\\tProcess Name:\\t\\tC:\\\\Windows\\\\System32\\\\winlogon.exe\\r\\n\\r\\nNetwork Information:\\r\\n\\tNetwork Address:\\t127.0.0.1\\r\\n\\tPort:\\t\\t\\t0\\r\\n\\r\\nThis event is generated when a process attempts to log on an account by explicitly specifying that account’s credentials.  This most commonly occurs in batch-type configurations such as scheduled tasks, or when using the RUNAS command.\",\"Category\":\"Logon\",\"Opcode\":\"Info\",\"SubjectUserSid\":\"S-1-5-18\",\"SubjectUserName\":\"IE11WIN7$\",\"SubjectDomainName\":\"WORKGROUP\",\"SubjectLogonId\":\"0x3e7\",\"LogonGuid\":\"{00000000-0000-0000-0000-000000000000}\",\"TargetUserName\":\"IEUser\",\"TargetDomainName\":\"IE11WIN7\",\"TargetLogonGuid\":\"{00000000-0000-0000-0000-000000000000}\",\"TargetServerName\":\"localhost\",\"TargetInfo\":\"localhost\",\"ProcessName\":\"C:\\\\Windows\\\\System32\\\\winlogon.exe\",\"IpAddress\":\"127.0.0.1\",\"IpPort\":\"0\",\"EventReceivedTime\":\"2015-09-13 18:02:36\",\"SourceModuleName\":\"eventlog\",\"SourceModuleType\":\"im_msvistalog\"}";

    @Mock
    private SearchHit hit;

    @Before
    public void setup() {
        fixture = new SearchHitToEventModelSerializer();
    }

    private Map<String, Object> toMap(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<HashMap<String, Object>>(){});
    }

    @Test
    public void testSerializeFor4648SearchHit() throws ParseException, IOException {
        when(hit.getSource()).thenReturn(toMap(EVENT_4648));

        EventModel model = fixture.serialize(hit);

        long timestamp = LocalDateTime.of(2015, 9, 13, 18, 2, 33).toInstant(ZoneOffset.UTC).toEpochMilli();

        assertThat(model.getEventId(), is(WindowsLogEventId.LOG_IN));
        assertThat(model.getHostName(), is("IE11Win7"));
        assertThat(model.getUserName(), is("IEUser"));
        assertThat(model.getTimestamp(), is(timestamp));
    }


    @Test
    public void testSerializeFor4647SearchHit() throws ParseException, IOException {
        when(hit.getSource()).thenReturn(toMap(EVENT_4647));

        EventModel model = fixture.serialize(hit);

        long timestamp = LocalDateTime.of(2015, 9, 13, 18, 8, 59).toInstant(ZoneOffset.UTC).toEpochMilli();

        assertThat(model.getEventId(), is(WindowsLogEventId.LOG_OUT));
        assertThat(model.getHostName(), is("IE11Win7"));
        assertThat(model.getUserName(), is("IEUser"));
        assertThat(model.getTimestamp(), is(timestamp));
    }

    @Test
    public void testSerializeFor4803SearchHit() throws ParseException, IOException {
        when(hit.getSource()).thenReturn(toMap(EVENT_4803));

        EventModel model = fixture.serialize(hit);

        long timestamp = LocalDateTime.of(2015, 9, 12, 21, 34, 8).toInstant(ZoneOffset.UTC).toEpochMilli();

        assertThat(model.getEventId(), is(WindowsLogEventId.SCREENSAVER_INACTIVE));
        assertThat(model.getHostName(), is("IE11Win7"));
        assertThat(model.getUserName(), is("IEUser"));
        assertThat(model.getTimestamp(), is(timestamp));
    }

    @Test
    public void testSerializeFor4802SearchHit() throws ParseException, IOException {
        when(hit.getSource()).thenReturn(toMap(EVENT_4802));

        EventModel model = fixture.serialize(hit);

        long timestamp = LocalDateTime.of(2015, 9, 12, 21, 26, 07).toInstant(ZoneOffset.UTC).toEpochMilli();

        assertThat(model.getEventId(), is(WindowsLogEventId.SCREENSAVER_ACTIVE));
        assertThat(model.getHostName(), is("IE11Win7"));
        assertThat(model.getUserName(), is("IEUser"));
        assertThat(model.getTimestamp(), is(timestamp));
    }

    @Test
    public void testSerializeFor4800SearchHit() throws ParseException, IOException {
        when(hit.getSource()).thenReturn(toMap(EVENT_4800));

        EventModel model = fixture.serialize(hit);

        long timestamp = LocalDateTime.of(2015, 9, 12, 21, 19, 12).toInstant(ZoneOffset.UTC).toEpochMilli();

        assertThat(model.getEventId(), is(WindowsLogEventId.SCREEN_LOCK));
        assertThat(model.getHostName(), is("IE11Win7"));
        assertThat(model.getUserName(), is("IEUser"));
        assertThat(model.getTimestamp(), is(timestamp));
    }

    @Test
    public void testSerializeFor4801SearchHit() throws ParseException, IOException {
        when(hit.getSource()).thenReturn(toMap(EVENT_4801));

        EventModel model = fixture.serialize(hit);

        long timestamp = LocalDateTime.of(2015, 9, 12, 21, 14, 58).toInstant(ZoneOffset.UTC).toEpochMilli();

        assertThat(model.getEventId(), is(WindowsLogEventId.SCREEN_UNLOCK));
        assertThat(model.getHostName(), is("IE11Win7"));
        assertThat(model.getUserName(), is("IEUser"));
        assertThat(model.getTimestamp(), is(timestamp));
    }


    @Test(expected = UnsupportedOperationException.class)
    public void testSerializeForUnsupportedSearchHit() throws ParseException {
        SearchHit hit = mock(SearchHit.class);

        Map<String, Object> result = ImmutableMap.of(
                "EventTime", "2015-09-12 21:19:12",
                "Hostname", "IE11Win7",
                "EventID", 1,
                "TargetUserName", "IEUser");

        when(hit.getSource()).thenReturn(result);
        fixture.serialize(hit);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSerializeForMissingFieldsSearchHit() throws ParseException {
        SearchHit hit = mock(SearchHit.class);

        Map<String, Object> result = ImmutableMap.of(
                "EventTime", "2015-09-12 21:19:12",
                "TargetUserName", "IEUser");

        when(hit.getSource()).thenReturn(result);
        fixture.serialize(hit);
    }

    @Test(expected = ParseException.class)
    public void testSerializeForInvalidDateFormatSearchHit() throws ParseException {
        SearchHit hit = mock(SearchHit.class);

        Map<String, Object> result = ImmutableMap.of(
                "EventTime", "2015-09-12",
                "Hostname", "IE11Win7",
                "EventID", 4801,
                "TargetUserName", "IEUser");

        when(hit.getSource()).thenReturn(result);
        fixture.serialize(hit);
    }
}
