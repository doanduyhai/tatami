/* jQuery jqEasyCharCounter plugin
 * Examples and documentation at: http://www.jqeasy.com/
 * Version: 1.0 (05/07/2010)
 * No license. Use it however you want. Just keep this notice included.
 * Requires: jQuery v1.3+
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
(function($) {

$.fn.extend({
    jqEasyCounter: function(givenOptions) {
        return this.each(function() {
            var $this = $(this),
                options = $.extend({
                    maxChars: 100,
					maxCharsWarning: 80,
					msgFontSize: '12px',
					msgFontColor: '#000000',
					msgFontFamily: 'Arial',
					msgTextAlign: 'right',
					msgWarningClass: 'badge-warning',
					msgErrorClass: 'badge-error',
					msgShortUrl: false,
					msgShortUrlLength: 13,
					msgUrlPattern : '((?:(?:https?|ftp|file)://|www)[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])',
					msgAppendMethod: 'insertAfter'
                }, givenOptions);
	
			if(options.maxChars <= 0) return;
			
			var shortURLRegExp = new RegExp(options.msgUrlPattern,"g");
				
			// create counter element
			var jqEasyCounterMsg = $("<div class=\"jqEasyCounterMsg\"><span class='badge'></span><span class='shortUrl'></span></div>");
			var jqEasyCounterMsgStyle = {
				'font-size' : options.msgFontSize,
				'font-family' : options.msgFontFamily,
				'color' : options.msgFontColor,
				'text-align' : options.msgTextAlign,
				'width' : $this.width(),
				'opacity' : 0
			};
			jqEasyCounterMsg.css(jqEasyCounterMsgStyle);
			// append counter element to DOM
			jqEasyCounterMsg[options.msgAppendMethod]($this);
			
			// bind events to this element
			$this
				.bind('keydown keyup keypress', doCount)
				.bind('focus paste', function(){setTimeout(doCount, 10);})
				.bind('blur', function(){jqEasyCounterMsg.stop().fadeTo( 'fast', 0);return false;});
			
			function doCount(){
				var val = $this.val(), length = val.length;
				if(options.msgShortUrl)
				{
					var urls = val.match(shortURLRegExp);
					for(i in urls)
					{
						length = length - urls[i].length +options.msgShortUrlLength;
					};
				}	
				
				if(length > options.maxChars) {
					length = options.maxChars - length;
				};
				
				
				if(length < 0){
					jqEasyCounterMsg.find('.badge').removeClass(options.msgWarningClass).addClass(options.msgErrorClass);
				}
				else if(length >= options.maxCharsWarning)
				{
					jqEasyCounterMsg.find('.badge').removeClass(options.msgErrorClass).addClass(options.msgWarningClass);
				}
				else {
					console.log('Reset classes');
					jqEasyCounterMsg.find('.badge').removeClass(options.msgWarningClass+' '+options.msgErrorClass);
				};
				
				jqEasyCounterMsg
					.find('.badge').html(length + "/" + options.maxChars);
				if(options.msgShortUrl)
				{
					jqEasyCounterMsg.find('.shortUrl').html(' ShortUrl size: '+options.msgShortUrlLength+' characters');
				}
                jqEasyCounterMsg.stop().fadeTo( 'fast', 1);
			};
        });
    }
});

})(jQuery);