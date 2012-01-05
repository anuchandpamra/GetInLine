//
//  Controller.m
//  iOS
//
//  Created by ANUPAM CHANDRA on 12/30/11.
//  Copyright 2011 SoftExcel Technologies Inc.,. All rights reserved.
//

#import "Controller.h"
#import "SBJson.h"
#import "QueueServerSocketConnection.h"

@interface Controller() 
	
	@property(nonatomic,retain) QueueServerSocketConnection* connection;

@end

NSString * const hostDNS = @"ec2-174-129-160-102.compute-1.amazonaws.com";

@implementation Controller

@synthesize connection;

// Called after all the GUI items have been instantiated
- (void) awakeFromNib{
	connection = [[QueueServerSocketConnection alloc] init:hostDNS andPort:7201];
	connection.listener = self;
	[connection connect];
}

// Cleanup
- (void)dealloc {
	self.connection = nil;
	[myTokenBuffer release];
	[super dealloc];
}

- (void)connectionAttemptFailed:(QueueServerSocketConnection*)connection {
	nextAvailableToken.text = @"Socket Connection Failed";
}


- (void)connectionTerminated:(QueueServerSocketConnection*)connection {
	nextAvailableToken.text = @"Socket Connection Terminated";
}

- (void) receiveNewToken:(int)token {
	nextAvailableToken.text = [NSString stringWithFormat:@"%d", token];	
}

- (IBAction)getNextToken:sender {
	NSURLRequest *request = [NSURLRequest requestWithURL:[NSURL URLWithString:[NSString stringWithFormat:@"http://%@:8080/Lift-1.0/dispense/token.json", hostDNS]]];
	myTokenBuffer =[[NSMutableData alloc] init];
	[[NSURLConnection alloc] initWithRequest:request delegate:self];
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response {
	[myTokenBuffer setLength:0];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data {
	[myTokenBuffer appendData:data];
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error {
	myTokenNumber.text = [NSString stringWithFormat:@"Connection failed: %@", [error description]];
}

- (void)connectionDidFinishLoading:(NSURLConnection *)conn {
	[conn release];
	
	NSString *responseString = [[NSString alloc] initWithData:myTokenBuffer encoding:NSUTF8StringEncoding];
	[myTokenBuffer release];
	
	NSDictionary *dictionary = [responseString JSONValue];
	myTokenNumber.text = [NSString stringWithFormat:@"%@", [dictionary objectForKey:@"number"]];
}
@end
