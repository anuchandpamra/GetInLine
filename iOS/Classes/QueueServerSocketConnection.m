//
//  QueueServerSocketConnection.m
//  iOS
//
//  Created by ANUPAM CHANDRA on 12/31/11.
//  Copyright 2011 SoftExcel Technologies Inc.,. All rights reserved.
//

#import "QueueServerSocketConnection.h"


// Declare C callback functions
void readStreamEventHandler(CFReadStreamRef stream, CFStreamEventType eventType, void *info);

// Private properties and methods
@interface QueueServerSocketConnection ()

// Properties
@property(nonatomic,retain) NSString* host;
@property(nonatomic,assign) int port;

// Clean
- (void)clean;

//Setup socket streams after opening the socket
- (BOOL)setupSocketStreams;

// Stream event handlers
- (void)readStreamHandleEvent:(CFStreamEventType)event;

// Read all available bytes from the read stream into buffer and try to extract packets
- (void)readFromStreamIntoIncomingBuffer;



@end


@implementation QueueServerSocketConnection

@synthesize listener;
@synthesize host, port;

// Reset
- (void)clean {  
	readStream = nil;
	readStreamOpen = NO;
	incomingDataBuffer = nil;
	self.host = nil;
}


// Release memory
- (void)dealloc {
	self.host = nil;
	self.listener = nil;
	[super dealloc];
}


// Initialize
- (id)init:(NSString*)_host andPort:(int)_port {
	[self clean];
	
	self.host = _host;
	self.port = _port;
	return self;
}



// Connect to the host at the given port
- (BOOL)connect {
	if ( self.host != nil ) {
		// Bind read/write streams to a new socket
		CFStreamCreatePairWithSocketToHost(kCFAllocatorDefault, (CFStringRef)self.host,
										   self.port, &readStream, &dummyWriteStream);
		
		return [self setupSocketStreams];
	}
	
	// No host - cannot connect
	return NO;
}


// Further setup socket streams that were created by one of our 'init' methods
- (BOOL)setupSocketStreams {
	// Make sure streams were created correctly
	if ( readStream == nil ) {
		[self close];
		return NO;
	}
	
	// Create buffers
	incomingDataBuffer = [[NSMutableData alloc] init];
	
	// Indicate that we want socket to be closed whenever streams are closed
	CFReadStreamSetProperty(readStream, kCFStreamPropertyShouldCloseNativeSocket,
							kCFBooleanTrue);
	
	// We will be handling the following stream events
	CFOptionFlags registeredEvents = kCFStreamEventOpenCompleted |
	kCFStreamEventHasBytesAvailable | kCFStreamEventCanAcceptBytes |
	kCFStreamEventEndEncountered | kCFStreamEventErrorOccurred;
	
	// Setup stream context - reference to 'self' will be passed to stream event handling callbacks
	CFStreamClientContext ctx = {0, self, NULL, NULL, NULL};
	
	// Specify callbacks that will be handling stream events
	CFReadStreamSetClient(readStream, registeredEvents, readStreamEventHandler, &ctx);
	
	// Schedule streams with current run loop
	CFReadStreamScheduleWithRunLoop(readStream, CFRunLoopGetCurrent(),
									kCFRunLoopCommonModes);
	
	// Open both streams
	if ( ! CFReadStreamOpen(readStream)) {
		[self close];
		return NO;
	}
	
	return YES;
}


// Close connection
- (void)close {
	// Cleanup read stream
	if ( readStream != nil ) {
		CFReadStreamUnscheduleFromRunLoop(readStream, CFRunLoopGetCurrent(), kCFRunLoopCommonModes);
		CFReadStreamClose(readStream);
		CFRelease(readStream);
		readStream = NULL;
	}
	
	
	// Cleanup buffers
	[incomingDataBuffer release];
	incomingDataBuffer = NULL;
	
	
	// Reset all other variables
	[self clean];
}


#pragma mark Read stream methods

// Dispatch readStream events
void readStreamEventHandler(CFReadStreamRef stream, CFStreamEventType eventType,
							void *info) {
	QueueServerSocketConnection* connection = (QueueServerSocketConnection*)info;
	[connection readStreamHandleEvent:eventType];
}


// Handle events from the read stream
- (void)readStreamHandleEvent:(CFStreamEventType)event {
	// Stream successfully opened
	if ( event == kCFStreamEventOpenCompleted ) {
		readStreamOpen = YES;
	}
	// New data has arrived
	else if ( event == kCFStreamEventHasBytesAvailable ) {
		// Read as many bytes from the stream as possible and try to extract meaningful packets
		[self readFromStreamIntoIncomingBuffer];
	}
	// Connection has been terminated or error encountered (we treat them the same way)
	else if ( event == kCFStreamEventEndEncountered || event == kCFStreamEventErrorOccurred ) {
		// Clean everything up
		[self close];
		
		// If we haven't connected yet then our connection attempt has failed
		if ( !readStreamOpen) {
			[listener connectionAttemptFailed:self];
		}
		else {
			[listener connectionTerminated:self];
		}
	}
}


// Read as many bytes from the stream as possible and try to extract meaningful packets
- (void)readFromStreamIntoIncomingBuffer {
	// Temporary buffer to read data into
	UInt8 buf[1024];
	int tokenNumber;
	
	// Try reading while there is data
	while( CFReadStreamHasBytesAvailable(readStream) ) {  
		CFIndex len = CFReadStreamRead(readStream, buf, sizeof(buf));
		if ( len <= 0 ) {
			// Either stream was closed or error occurred. Close everything up and treat this as "connection terminated"
			[self close];
			[listener connectionTerminated:self];
			return;
		}
		
		[incomingDataBuffer appendBytes:buf length:len];
	}
	
	// Try to extract token number from the buffer.	
	// We might have more than one token in the buffer - that's why we'll be reading it inside the while loop
	while( YES ) {
		// Did we read the header yet?
			// Do we have enough bytes in the buffer to read the header?
		if ( [incomingDataBuffer length] >= sizeof(int) ) {
			// extract token number
			memcpy(&tokenNumber, [incomingDataBuffer bytes], sizeof(int));
			
			// Send the token number to the listener
			[listener receiveNewToken:CFSwapInt32BigToHost(tokenNumber)];
			
			// remove that chunk from buffer
			NSRange rangeToDelete = {0, sizeof(int)};
			[incomingDataBuffer replaceBytesInRange:rangeToDelete withBytes:NULL length:0];
		}
		else {
			// We don't have enough yet. Will wait for more data.
			break;
		}
		
	}
}


@end
