<StyledLayerDescriptor version="1.0.0">
	<NamedLayer>
		<Name>Calles</Name>
			<UserStyle>
				<Title>Calles</Title>
				<FeatureTypeStyle>
					<Rule>
											
						<Title> </Title>
						<LineSymbolizer>
							<Stroke>
								<CssParameter name="stroke">#787878</CssParameter>
								<CssParameter name="stroke-width">1</CssParameter>
							</Stroke>
						</LineSymbolizer>
						<TextSymbolizer>
							<Label>
								<PropertyName>nombre</PropertyName>
							</Label>
							<LabelPlacement>
								<LinePlacement></LinePlacement>
							</LabelPlacement>
							<Font>
								<CssParameter name="font-family">Lucida Sans</CssParameter>
								<CssParameter name="font-size">10</CssParameter>
							</Font>
							<Fill>
								<CssParameter name="fill">#000000</CssParameter>
							</Fill>
						</TextSymbolizer>
                        <MaxScaleDenominator>15000</MaxScaleDenominator> 
					</Rule>
				</FeatureTypeStyle>
			</UserStyle>
		</NamedLayer>
</StyledLayerDescriptor>