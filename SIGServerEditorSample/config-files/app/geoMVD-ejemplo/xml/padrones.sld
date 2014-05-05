<StyledLayerDescriptor version="1.0.0">
	<NamedLayer>
		<Name>poligonos</Name>
			<UserStyle>
				<Title>Estilo Padrones</Title>
				<FeatureTypeStyle>
					<Rule>
					<title> </title>
						<PolygonSymbolizer>
							<Fill>
								<CssParameter name="fill">#ffcc99</CssParameter>
							    <CssParameter name="fill-opacity">
							       0.20
							    </CssParameter>	
							</Fill>
							<Stroke>
								<CssParameter name="stroke">#999999</CssParameter>
							</Stroke>
						</PolygonSymbolizer>
						
						
						<TextSymbolizer>
							<Label>
								<PropertyName>padron</PropertyName>
							</Label>
							<Font>
								<CssParameter name="font-family">Tahoma</CssParameter>
								<CssParameter name="font-size">10</CssParameter>
							</Font>
							<Fill>
								<CssParameter name="fill">#000000</CssParameter>
							</Fill>
						</TextSymbolizer>						
						<MaxScaleDenominator>2500</MaxScaleDenominator>
					</Rule>
				</FeatureTypeStyle>
			</UserStyle>
		</NamedLayer>
</StyledLayerDescriptor>