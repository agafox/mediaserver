package org.mobicents.media.server.io.sdp;

import java.util.HashMap;
import java.util.Map;

import org.mobicents.media.server.io.sdp.attributes.ConnectionModeAttribute;
import org.mobicents.media.server.io.sdp.attributes.FormatParameterAttribute;
import org.mobicents.media.server.io.sdp.attributes.MaxPacketTimeAttribute;
import org.mobicents.media.server.io.sdp.attributes.PacketTimeAttribute;
import org.mobicents.media.server.io.sdp.attributes.RtpMapAttribute;
import org.mobicents.media.server.io.sdp.dtls.attributes.FingerprintAttribute;
import org.mobicents.media.server.io.sdp.fields.AttributeField;
import org.mobicents.media.server.io.sdp.fields.ConnectionField;
import org.mobicents.media.server.io.sdp.fields.MediaDescriptionField;
import org.mobicents.media.server.io.sdp.fields.OriginField;
import org.mobicents.media.server.io.sdp.fields.SessionNameField;
import org.mobicents.media.server.io.sdp.fields.TimingField;
import org.mobicents.media.server.io.sdp.fields.VersionField;
import org.mobicents.media.server.io.sdp.ice.attributes.CandidateAttribute;
import org.mobicents.media.server.io.sdp.ice.attributes.IceLiteAttribute;
import org.mobicents.media.server.io.sdp.ice.attributes.IcePwdAttribute;
import org.mobicents.media.server.io.sdp.ice.attributes.IceUfragAttribute;
import org.mobicents.media.server.io.sdp.rtcp.attributes.RtcpAttribute;
import org.mobicents.media.server.io.sdp.rtcp.attributes.RtcpMuxAttribute;

/**
 * 
 * @author Henrique Rosa (henrique.rosa@telestax.com)
 *
 */
public class SessionDescription {
	
	private static final String NEWLINE = "\n";
	
	private static final String PARSE_ERROR = "Cannot parse SDP: ";
	private static final String PARSE_ERROR_EMPTY = PARSE_ERROR + "empty";
	
	// SDP fields (session-level)
	private VersionField version;
	private OriginField origin;
	private SessionNameField sessionName;
	private ConnectionField connection;
	private TimingField timing;
	private ConnectionModeAttribute connectionMode;
	
	// ICE attributes (session-level)
	private IceLiteAttribute iceLite;
	private IcePwdAttribute icePwd;
	private IceUfragAttribute iceUfrag;
	
	// WebRTC attributes (session-level)
	private FingerprintAttribute fingerprint;

	// Media Descriptions
	private final Map<String, MediaDescriptionField> mediaMap;
	private MediaDescriptionField currentMedia;
	
	// PARSING
	private final SdpParserPipeline parsers = new SdpParserPipeline();
	   
	public SessionDescription() {
		this.mediaMap = new HashMap<String, MediaDescriptionField>(5);
	}
	
	public VersionField getVersion() {
		return version;
	}
	
	public OriginField getOrigin() {
		return origin;
	}
	
	public SessionNameField getSessionName() {
		return sessionName;
	}
	
	public ConnectionField getConnection() {
		return connection;
	}
	
	public TimingField getTiming() {
		return timing;
	}
	
	public MediaDescriptionField getMediaDescription(String mediaType) {
		return this.mediaMap.get(mediaType);
	}
	
	public void parse(String sdp) throws SdpException {
		if(sdp == null || sdp.isEmpty()) {
			throw new SdpException(PARSE_ERROR_EMPTY);
		}
		
		// Process each line of SDP
		String[] lines = sdp.split(NEWLINE);
		for (String line : lines) {
			parseLine(line);
		}
		
		// Clean pointer to current media descriptor
		this.currentMedia = null;
	}
	
	private void parseLine(String line) throws SdpException {
		try {
			char fieldType = line.charAt(0);
			switch (fieldType) {
			case AttributeField.FIELD_TYPE:
				int separator = line.indexOf(AttributeField.ATTRIBUTE_SEPARATOR);
				String attributeType = (separator == - 1) ? line.substring(2) : line.substring(2, separator);
				
				SdpParser<? extends AttributeField> attributeParser = this.parsers.getAttributeParser(attributeType);
				if(attributeParser != null) {
					convertAndApplyAttribute(attributeParser.parse(line));
				}
				break;
			default:
				SdpParser<? extends SdpField> fieldParser = this.parsers.getFieldParser(fieldType);
				if(fieldParser != null) {
					convertAndApplyField(fieldParser.parse(line));
				}
				break;
			}
		} catch (Exception e) {
			throw new SdpException("Could not parse SDP: " + line, e);
		}
	}
	
	private void convertAndApplyField(SdpField field) {
		switch (field.getFieldType()) {
		case VersionField.FIELD_TYPE:
			this.version = (VersionField) field;
			break;

		case OriginField.FIELD_TYPE:
			this.origin = (OriginField) field;
			break;

		case SessionNameField.FIELD_TYPE:
			this.sessionName = (SessionNameField) field;

			break;
		case TimingField.FIELD_TYPE:
			this.timing = (TimingField) field;
			break;

		case ConnectionField.FIELD_TYPE:
			if(this.currentMedia == null) {
				this.connection = (ConnectionField) field;
			} else {
				this.currentMedia.setConnection((ConnectionField) field);
			}
			break;

		case MediaDescriptionField.FIELD_TYPE:
			MediaDescriptionField mediaField = (MediaDescriptionField) field;
			this.currentMedia = mediaField;
			this.mediaMap.put(mediaField.getMedia(), mediaField);
			break;

		default:
			// Ignore unsupported type
			break;
		}
	}
	
	private void convertAndApplyAttribute(AttributeField attribute) {
		switch (attribute.getKey()) {
		case RtpMapAttribute.ATTRIBUTE_TYPE:
			// TODO apply rtpmap attribute
			break;
			
		case FormatParameterAttribute.ATTRIBUTE_TYPE:
			// TODO apply fmt attribute
			break;
		
		case PacketTimeAttribute.ATTRIBUTE_TYPE:
			// TODO apply ptime attribute
			break;

		case MaxPacketTimeAttribute.ATTRIBUTE_TYPE:
			// TODO apply maxptime attribute
			break;
			
		case ConnectionModeAttribute.SENDONLY:
		case ConnectionModeAttribute.RECVONLY:
		case ConnectionModeAttribute.SENDRECV:
		case ConnectionModeAttribute.INACTIVE:
			if(this.currentMedia == null) {
				this.connectionMode = (ConnectionModeAttribute) attribute;
			} else {
				this.currentMedia.setConnectionMode((ConnectionModeAttribute) attribute);
			}
			break;
			
		case RtcpAttribute.ATTRIBUTE_TYPE:
			this.currentMedia.setRtcp((RtcpAttribute) attribute);
			break;
			
		case RtcpMuxAttribute.ATTRIBUTE_TYPE:
			this.currentMedia.setRtcpMux((RtcpMuxAttribute) attribute);
			break;
			
		case IceLiteAttribute.ATTRIBUTE_TYPE:
			this.iceLite = (IceLiteAttribute) attribute;
			break;
			
		case IceUfragAttribute.ATTRIBUTE_TYPE:
			if(this.currentMedia == null) {
				this.iceUfrag = (IceUfragAttribute) attribute;
			} else {
				this.currentMedia.setIceUfrag((IceUfragAttribute) attribute);
			}
			break;
			
		case IcePwdAttribute.ATTRIBUTE_TYPE:
			if(this.currentMedia == null) {
				this.icePwd = (IcePwdAttribute) attribute;
			} else {
				this.currentMedia.setIcePwd((IcePwdAttribute) attribute);
			}
			break;
			
		case CandidateAttribute.ATTRIBUTE_TYPE:
			this.currentMedia.addCandidate((CandidateAttribute) attribute);
			break;
			
		case FingerprintAttribute.ATTRIBUTE_TYPE:
			if(this.currentMedia == null) {
				this.fingerprint = (FingerprintAttribute) attribute;
			} else {
				this.currentMedia.setFingerprint((FingerprintAttribute) attribute);
			}
			break;
			
		default:
			break;
		}
	}
	
}
