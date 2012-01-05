//
//  QueueServerSocketConnection.m
//  iOS
//
//  Created by ANUPAM CHANDRA on 12/31/11.
//  Copyright 2011 SoftExcel Technologies Inc.,. All rights reserved.
//

#import "QueueServerSocketConnection.h"


// Declare C callback functions
void inputDataStreamEventHandler(CFReadStreamRef stream, CFStreamEventType eventType, void *info);

// Private properties and methods
@interface QueueServerSocketConnection ()

// Properties
@property(nonatomic,retain) NSString* host;
@property(nonatomic,assign) int port;

// Reset Connection settings
- (void)reset;

//Setup socket streams after opening the socket
- (BOOL)setupInputDataStream;

// Stream event handlers
- (void)inputDataStreamHandleEvent:(CFStreamEventType)event;

// Read all available bytes from the read stream into buffer and try to extract packets
- (void)inputDataStreamToInputDataBuffer;



@end


@implementation QueueServerSocketConnection

@synthesize listener;
@synthesize host, port;

// Reset
- (void)reset {  
	inputDataStream = nil;
	inputDataStreamOpen = NO;
	inputDataBuffer = nil;
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
	[self reset];
	
	self.host = _host;
	self.port = _port;
	return self;
}



// Connect to the host at the given port
- (BOOL)connect {
	if ( self.host != nil ) {
		// Bind read/write streams to a new socket
		CFStreamCreatePairWithSocketToHost(kCFAllocatorDefault, (CFStringRef)self.host,
										   self.port, &inputDataStream, NULL);
		
		return [self setupInputDataStream];
	}
	
	// No host - cannot connect
	return NO;
}


// Further setup socket streams that were created by one of our 'init' methods
- (BOOL)setupInputDataStream {
	// Make sure streams were created correctly
	if ( inputDataStream == nil ) {
		[self disconnect];
		return NO;
	}
	
	// Create buffer for reading data
	inputDataBuffer = [[NSMutableData alloc] init];
	
	// Sockets should close when streams close
	CFReadStreamSetProperty(inputDataStream, kCFStreamPropertyShouldCloseNativeSocket,
							kCFBooleanTrue);
	
	// Handle following stream events
	CFOptionFlags registeredEvents = 
	kCFStreamEventOpenCompleted |
	kCFStreamEventHasBytesAvailable | 
	kCFStreamEventCanAcceptBytes |
	kCFStreamEventEndEncountered | 
	kCFStreamEventErrorOccurred;
	
	// Setup stream context - reference to 'self' will be passed to stream event handling callbacks
	CFStreamClientContext ctx = {0, self, NULL, NULL, NULL};
	
	// Specify callbacks that will be handling stream events
	CFReadStreamSetClient(inputDataStream, registeredEvents, inputDataStreamEventHandler, &ctx);
	
	// Schedule streams with current run loop
	CFReadStreamScheduleWithRunLoop(inputDataStream, CFRunLoopGetCurrent(),
									kCFRunLoopCommonModes);
	
	// Open read stream
	if ( ! CFReadStreamOpen(inputDataStream)) {
		[self disconnect];
		return NO;
	}
	
	return YES;
}


// Close connection
- (void)disconnect {
	// Cleanup read stream
	if ( inputDataStream != nil ) {
		CFReadStreamUnscheduleFromRunLoop(inputDataStream, CFRunLoopGetCurrent(), kCFRunLoopCommonModes);
		CFReadStreamClose(inputDataStream);
		CFRelease(inputDataStream);
		inputDataStream = NULL;
	}
	
	
	// Cleanup buffers
	[inputDataBuffer release];
	inputDataBuffer = NULL;
	
	
	// Reset all other variables
	[self reset];
}


#pragma mark Read stream methods

// Dispatch readStream events
void inputDataStreamEventHandler(CFReadStreamRef stream, CFStreamEventType eventType,
							void *info) {
	QueueServerSocketConnection* connection = (QueueServerSocketConnection*)info;
	[connection inputDataStreamHandleEvent:eventType];
}


// Handle events from the read stream
- (void)inputDataStreamHandleEvent:(CFStreamEventType)event {
	// Stream opened
	if ( event == kCFStreamEventOpenCompleted ) {
		inputDataStreamOpen = YES;
	}
	// Data available for reading
	else if ( event == kCFStreamEventHasBytesAvailable ) {
		// Read as many bytes from the stream as possible; extract a token
		[self inputDataStreamToInputDataBuffer];
	}
	// Connection has been terminated or error encountered (we treat them the same way)
	else if ( event == kCFStreamEventEndEncountered || event == kCFStreamEventErrorOccurred ) {
		// Clean everything up
		[self disconnect];
		
		// If we haven't connected yet then our connection attempt has failed
		if ( !inputDataStreamOpen) {
			[listener connectionAttemptFailed:self];
		}
		else {
			[listener connectionTerminated:self];
		}
	}
}


// Read as many bytes from the stream as possible and get token numbers
- (void)inputDataStreamToInputDataBuffer {
	// Temporary buffer to read data into
	UInt8 buf[1024];
	int tokenNumber;
	
	// Try reading while there is data
	while( CFReadStreamHasBytesAvailable(inputDataStream) ) {  
		CFIndex len = CFReadStreamRead(inputDataStream, buf, sizeof(buf));
		if ( len <= 0 ) {
			// Either stream was closed or error occurred. Close everything up and treat this as "connection terminated"
			[self disconnect];
			[listener connectionTerminated:self];
			return;
		}
		
		[inputDataBuffer appendBytes:buf length:len];
	}
	
	// Try to extract token number from the buffer.	
	// We might have more than one token in the buffer - that's why we'll be reading it inside the while loop
	while( YES ) {
		// If we got a complete integer, treat it as the next token number
		if ( [inputDataBuffer length] >= sizeof(int) ) {
			// extract token number
			memcpy(&tokenNumber, [inputDataBuffer bytes], sizeof(int));
			
			// Send the token number to the listener
			//
			if (CFByteOrderGetCurrent() == CFByteOrderLittleEndian){
				[listener receiveNewToken:CFSwapInt32BigToHost(tokenNumber)];
			}
			else {
				[listener receiveNewToken:tokenNumber];
			}
			
			// remove that chunk from buffer
			NSRange rangeToDelete = {0, sizeof(int)};
			[inputDataBuffer replaceBytesInRange:rangeToDelete withBytes:NULL length:0];
		}
		else {
			// We don't have enough yet. Will wait for more data.
			break;
		}
		
	}
}


@end
